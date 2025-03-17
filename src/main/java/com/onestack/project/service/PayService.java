package com.onestack.project.service;

import com.onestack.project.domain.MemPay;
import com.onestack.project.domain.MemProEstimation;
import com.onestack.project.domain.Pay;
import com.onestack.project.domain.Professional;
import com.onestack.project.mapper.PayMapper;
import com.onestack.project.mapper.ProMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PayService {

    @Autowired
    private PayMapper payMapper;

    @Value("${PORTONE_API_KEY}")
    private String apiKey;

    @Value("${PORTONE_API_SECRET}")
    private String apiSecret;
    @Autowired
    private ProMapper proMapper;

    // 견적서 폼 요청
    public MemProEstimation getPayForm(int estimationNo) {

        MemProEstimation payList = payMapper.getPayForm(estimationNo);

        return payList;
    }




    // 포트원 카카오페이 인증 토큰 발급
    private String getAccessToken() throws Exception {
        RestTemplate restTemplate = new RestTemplate(); // HTTP 요청을 보낼 때 사용하는 Spring 클래스
        String url = "https://api.iamport.kr/users/getToken"; // 포트원 API의 토큰 발급 엔드포인트 URL

        // 요청 바디를 JSON 형식으로 구성
        JSONObject requestBody = new JSONObject();
        requestBody.put("imp_key", apiKey); // API 키 추가
        requestBody.put("imp_secret", apiSecret); // API 시크릿 추가

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON); // 요청 타입을 JSON으로 지정
        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers); // 요청 데이터와 헤더를 포함한 HTTP 엔터티 생성

        // POST 요청을 전송하고 응답받기
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // 응답 상태 코드가 200 OK인지 확인
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject responseBody = new JSONObject(response.getBody()); // 응답 본문을 JSON 객체로 변환
            if (responseBody.getInt("code") == 0) { // API 응답 코드가 성공(0)인지 확인
                return responseBody.getJSONObject("response").getString("access_token"); // 액세스 토큰 반환
            } else {
                throw new Exception("토큰 발급 실패: " + responseBody.getString("message")); // 실패 메시지 포함하여 예외 던짐
            }
        } else {
            throw new Exception("토큰 발급 요청 실패: " + response.getStatusCode()); // HTTP 상태 코드 포함하여 예외 던짐
        }
    }


    // 결제 검증
    public boolean verifyPayment(String impUid, int estimationNo, int paidAmount) throws Exception {
        // 1. 먼저 토큰 발급 메서드를 호출하여 액세스 토큰 가져오기
        String accessToken = getAccessToken();

        // 2. 결제 정보를 가져오기 위해 API 호출
        RestTemplate restTemplate = new RestTemplate(); // 새로운 HTTP 요청을 보내기 위해 RestTemplate 객체 생성
        String url = "https://api.iamport.kr/payments/" + impUid; // impUid를 포함한 결제 조회 URL 구성

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken); // Authorization 헤더에 액세스 토큰 추가
        HttpEntity<Void> entity = new HttpEntity<>(headers); // 헤더만 포함된 HTTP 엔터티 생성

        // GET 요청을 전송하고 응답받기
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // 3. 응답 상태 코드가 200 OK인지 확인
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject responseBody = new JSONObject(response.getBody()); // 응답 본문을 JSON 객체로 변환
            JSONObject paymentData = responseBody.getJSONObject("response"); // 결제 데이터 추출

            // 결제 상태와 금액을 추출
            double amount = paymentData.getDouble("amount"); // 결제 금액 추출
            String status = paymentData.getString("status"); // 결제 상태 추출

            // 4. DB에서 해당 주문의 금액을 조회
            double orderAmount = payMapper.getPrice(estimationNo); // 주문 금액 조회 (DB에서)

            // 5. 결제 검증 로직: 금액과 상태가 일치하는지 확인
            if (amount == orderAmount && "paid".equals(status) && paidAmount == amount) { // 금액과 상태, paidAmount 비교
                return true; // 결제 검증 성공 시 true 반환
            } else {
                throw new Exception("결제 검증 실패: 금액, 상태 또는 paidAmount 불일치"); // 검증 실패 시 예외 던짐
            }
        } else {
            throw new Exception("결제 정보 조회 실패: " + response.getStatusCode()); // HTTP 상태 코드 포함하여 예외 던짐
        }
    }


    // 결제 DB 저장
    public void insertPay(Pay pay)  {
        payMapper.insertPay(pay);
    }

    // 결제 완료 폼
    public MemPay getPayDoneForm(int payNo) {

        MemPay payDoneList = payMapper.getPayDoneForm(payNo);

        return payDoneList;
    }

    // 결제 번호 가져오기
    public int getPayNo(int estimationNo) {
        return payMapper.getPayNo(estimationNo);
    }

    // 전문가 평균 가격 수정
    public void updateAveragePrice(int payNo) {
        // payNo로 전문가 조회
        int estimationNo = payMapper.findByPayNo(payNo);
        int proNo = payMapper.findByEstimationNo(estimationNo);

        // proNo로 전문가 조회
        Professional professional = payMapper.getPro(proNo);

        // 방금 결제된 금액 가져오기
        MemPay memPay = payMapper.getPayDoneForm(payNo);
        int payAmount = memPay.getPay().getPayPrice();

        // 전문가의 이전까지 총 결제 금액 구하기
        int count = payMapper.getPayCount(proNo);
        int beforePrice = professional.getAveragePrice();
        int afterPrice = beforePrice*count;


        // 평균 가격 계산
        double newAveragePrice = (afterPrice + payAmount) / (count + 1);

        // 평균 가격 수정
        professional.setAveragePrice((int) newAveragePrice);

        // 평균 가격 불러오기
        int averagePrice = professional.getAveragePrice();

        // 매퍼를 통해 DB에 수정된 값 저장
        payMapper.updateProPrice(averagePrice, proNo);
    }

    // estimationNo 가져오기
    public int findByPayNo(int payNo) {
        return payMapper.findByPayNo(payNo);
    }

    // proNo 가져오기
    public int findByEstimationNo(int estimationNo) {
        return payMapper.findByEstimationNo(estimationNo);
    }

    // 결제 내역 가져오기
    public List<MemPay> getReceipt(int memberNo) {
        return payMapper.getReceipt(memberNo);
    }

    // 결제 횟수 가져오기
    public int getMemPayCount(int memberNo) {
        return payMapper.getMemPayCount(memberNo);
    }

    // 전문가 고용 횟수 증가
    public void updateStudentCount(int proNo) {payMapper.updateStudentCount(proNo);}

    // 결제 내역 페이징네이션
    public List<MemPay> getReceiptWithPaging(int memberNo, int offset, int pageSize) {return payMapper.getReceiptWithPaging(memberNo, offset, pageSize);}

}
