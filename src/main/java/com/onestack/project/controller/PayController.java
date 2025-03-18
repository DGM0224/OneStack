package com.onestack.project.controller;

import com.onestack.project.domain.*;
import com.onestack.project.mapper.PayMapper;
import com.onestack.project.service.ChatService;
import com.onestack.project.service.MemberService;
import com.onestack.project.service.PayService;
import com.onestack.project.service.ProService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class PayController {

    @Autowired
    PayService payService;
    @Autowired
    private ProService proService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private PayMapper payMapper;

    @Autowired
    private ChatService chatService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /* 결제 요청 폼 */
    @GetMapping("/payForm")
    public String getPayForm(Model model, int quotationNo) {

        MemProEstimation payList = payService.getPayForm(quotationNo);
        model.addAttribute(payList);

        return "views/payForm";
    }

    /* 결제 완료 폼 */
    @GetMapping("/payDoneForm")
    @ResponseBody
    public MemPay getPayDoneForm(@RequestParam int payNo) {
        MemPay payDone = payService.getPayDoneForm(payNo);
        return payDone;  // 이 객체는 자동으로 JSON 형식으로 변환됩니다.
    }


    /* 결제 검증 */
    @PostMapping("/api/payment/verify")
    public ResponseEntity<String> paymentVerify(@RequestBody PaymentVerificationRequest request, HttpSession session) throws Exception {

        log.info("/verify : " + request.getImpUid());
        boolean isVerified = payService.verifyPayment(request.getImpUid(), request.getEstimationNo(), request.getPaidAmount());
        Member member = (Member) session.getAttribute("member");

        int estimationNo = request.getEstimationNo();


        if (isVerified) {
            // 최종 결제 처리 - DB(결제 테이블) 작업

            Pay pay = new Pay();
            pay.setEstimationNo(request.getEstimationNo()); // 주문 정보의 estimationNo
            pay.setMemberNo(request.getMemberNo()); // 주문 정보의 memberNo
            // 결제 타입
            if(request.getChannelNum() == 1) {
                pay.setPayType("카카오페이");
            } else if (request.getChannelNum() == 2) {
                pay.setPayType("KG이니시스");
            }
            pay.setPayContent(request.getPayContent()); // 결제 내용
            pay.setPayPrice(request.getPaidAmount()); // 결제 금액
            pay.setPayStatus(true); // 결제 완료 상태 (1)

            // 결제 정보 DB에 저장
            payService.insertPay(pay);

            // 시스템 메시지 전송
            ChatMessage systemMessage = new ChatMessage();
            systemMessage.setRoomId(request.getRoom());
            systemMessage.setSender(member.getMemberId());
            systemMessage.setNickname(member.getNickname());
            systemMessage.setType("SYSTEM");
            systemMessage.setMessage("결제가 완료되었습니다. 프로젝트에 대한 평가를 리뷰로 남겨주세요!");
            systemMessage.setSentAt(LocalDateTime.now());

            // DB에 시스템 메시지 저장
            chatService.saveMessage(systemMessage);

            // 시스템 메시지를 웹소켓으로 전송
            messagingTemplate.convertAndSend("/topic/chat/room/" + request.getRoom(), systemMessage);

            // 방금 결제한 결제 내역의 payNo를 가져오기
            int payNo = payService.getPayNo(estimationNo);

            // proNo 가져오기
            int proNo = payService.findByEstimationNo(estimationNo);

            // 전문가 고용 수 증가
            payService.updateStudentCount(proNo);

            // 견적 상태 수정
            memberService.updateEstimationProgress(estimationNo, 3);


            return ResponseEntity.ok(String.valueOf(payNo));
        }

        return ResponseEntity.status(400).body("결제 검증 실패");
    }

    /* 전문가 평균 가격 수정 및 견적 과정 수정*/
    @PostMapping("/updateAveragePrice")
    public ResponseEntity<Map<String, String>> updateAveragePrice(@RequestBody Map<String, String> payload) {
        String payNo = payload.get("payNo");
        payService.updateAveragePrice(Integer.parseInt(payNo));

        Map<String, String> response = new HashMap<>();
        response.put("message", "전문가 평균 가격 수정 성공!");

        int estimationNo = payService.findByPayNo(Integer.parseInt(payNo));

        memberService.updateEstimationProgress(estimationNo, 3);

        return ResponseEntity.ok(response);
    }

    /* 결제 내역 폼 */
    @GetMapping("/payReceipt")
    public String payReceipt(Model model,
                           @SessionAttribute("member") Member member,
                           @RequestParam(defaultValue = "1") int page) {
        int memberNo = member.getMemberNo();
        int pageSize = 5; // 페이지당 표시할 항목 수
        int offset = (page - 1) * pageSize;

        List<MemPay> recipetList = payService.getReceiptWithPaging(memberNo, offset, pageSize);
        int totalPayCount = payService.getMemPayCount(memberNo);

        // 총 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalPayCount / pageSize);

        model.addAttribute("recipetList", recipetList);
        model.addAttribute("payCount", totalPayCount);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "views/payReceiptForm";
    }

    public List<MemPay> getReceiptWithPaging(int memberNo, int offset, int limit) {
        return payMapper.getReceiptWithPaging(memberNo, offset, limit);
    }









}
