package com.onestack.project.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.onestack.project.domain.*;
import com.onestack.project.service.ProService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.onestack.project.service.MemberService;
import com.onestack.project.service.ProfessionalService;
import com.onestack.project.service.ReviewService;
import com.onestack.project.service.SurveyService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ProfessionalController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private ProfessionalService professionalService;

	@Autowired
	private ProService proService;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private ReviewService reviewService;

	/* itemNo에 따른 필터링, 전문가 전체 리스트 출력 */
	@GetMapping("/findPro")

	public String getProList(Model model, @RequestParam(value = "itemNo") int itemNo) {

		Map<String, Object> surveyModelMap = proService.getFilter(itemNo);
		Map<String, Object> proModelMap = proService.getMemProAdCateInfo(itemNo);

		List<MemProAdInfoCate> proList = (List<MemProAdInfoCate>) proModelMap.get("proList");

		double overallAveragePrice = proList.stream()  // proList에서 스트림 처리
				.map(MemProAdInfoCate::getProfessional)  // MemProAdInfoCate에서 Professional 객체 추출
				.mapToDouble(Professional::getAveragePrice)  // Professional 객체에서 평균 가격 추출
				.average()  // 평균 계산
				.orElse(0.0);

		String formattedAveragePrice = String.format("%,d", (long) overallAveragePrice);

		model.addAllAttributes(surveyModelMap);
		model.addAllAttributes(proModelMap);
		model.addAttribute("itemNo", itemNo);
		model.addAttribute("overallAveragePrice", formattedAveragePrice);

		return "views/findPro";
	}

    /* 견적 요청서 폼 */
	@GetMapping("/estimationForm")
	public String getEstimationForm(Model model, @RequestParam(value = "proNo") int proNo) {

		model.addAttribute("proNo", proNo);

		return "views/estimationForm";
	}

    /* 견적 요청서 작성 */
	@PostMapping("/submitEstimation")
	public String submitEstimation(Estimation estimation, @RequestParam("proNo") int proNo) {
		try {
			// 견적 정보 설정
			estimation.setProNo(proNo);
			estimation.setProgress(0); // 초기 상태 (요청)
			
			// 견적 저장
			proService.submitEstimation(estimation);
			
			return "redirect:/estimationDoneForm";
		} catch (Exception e) {
			log.error("견적 요청 중 오류 발생", e);
			return "redirect:/error";
		}
	}

    /* 견적 요청 완료 페이지 */
    @GetMapping("/estimationDoneForm")
    public String estimationDoneForm() {
        return "views/estimationDoneForm";
    }


    /* 전문가 상세보기 */
    @GetMapping("/proDetail")
    public String getProDetail(Model model, @RequestParam(value = "proNo") int proNo) {
        List<MemberWithProfessional> proList = professionalService.getPro2(proNo);
        List<Review> reviewList = reviewService.getReviewList(proNo);

        int sum = 0;
        int cnt = 0;
        for(Review review : reviewList) {
            sum += review.getReviewRate();
            cnt++;
        }

        sum /= cnt;

        model.addAttribute("proList", proList);
        model.addAttribute("reviewList", reviewList);
        model.addAttribute("reviewRateAvg", sum);

        return "views/proDetail";
    }


    /* 설문조사 페이지 */
    @GetMapping("/survey")
    public String getSurveyForm(@RequestParam("itemNo") int itemNo, Model model) {
        Map<String, Object> surveyData = surveyService.getSurvey(itemNo);
        model.addAllAttributes(surveyData);
        return "views/surveypage";
    }

    /* 전문가 정보 저장 */
    @GetMapping("/proConversion")
    public String getProConversion(HttpSession session, Model model) {
        String memberId = (String) session.getAttribute("memberId");
        if (memberId == null) {
            return "redirect:/login";
        }
        int memberNo = memberService.getMemberById(memberId);
        model.addAttribute("member", memberService.getMember(memberId));
        model.addAttribute("categories", surveyService.getAllCategories());

        return "views/proConversion";
    }

    /* 전문가 정보 저장 */
    @PostMapping("/proConversion/save")
    @ResponseBody
    public ResponseEntity<?> saveProfessionalData(@RequestBody ProConversionRequest request) {
        try {
            log.info("수신된 데이터: {}", request);

            // 빈 Survey Answer 제거
            List<String> filteredAnswers = request.getSurveyAnswers().stream()
                    .filter(answer -> answer != null && !answer.trim().isEmpty())
                    .toList();
            request.setSurveyAnswers(filteredAnswers);

            // 데이터 저장
            professionalService.saveProConversionData(request);
            return ResponseEntity.ok(Collections.singletonMap("message", "전문가 신청이 완료되었습니다."));
        } catch (Exception e) {
            log.error("전문가 데이터 저장 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "저장 실패"));
        }
    }
}
