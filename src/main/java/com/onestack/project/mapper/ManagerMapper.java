package com.onestack.project.mapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.onestack.project.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ManagerMapper {
	
	public List<Member> getAllMember();

	List<MemProPortPaiCate> getMemProPortPaiCate();

//	void updateProStatus(@Param("proNo") int proNo,
//						 @Param("professorStatus") Integer professorStatus,
//						 @Param("screeningMsg") String screeningMsg);

	// Member 테이블 업데이트 (memberType 변경)
	void updateMemberType(@Param("proNo") int proNo,
						  @Param("professorStatus") Integer professorStatus);


	int checkDuplicateFields(Map<String, Object> params);

	void updateMember(Map<String, Object> params);

	public List<Member> getWithdrawalMembers();


	public Member getMember();

	void addReports(Reports reports);

	// 기간 정지 자동 해제
	void releaseSuspendMember();

	// 신고 대상 신고 횟수 증가
	int incrementReportedCount(int memberNo);

	// 신고 리스트 조회
	List<Reports> getReports();

	// ✅ 전문가의 포트폴리오 삭제

	void updateProStatus(@Param("proNo") int proNo,
						 @Param("professorStatus") Integer professorStatus,
						 @Param("screeningMsg") String screeningMsg);

	void updateMemberType(@Param("proNo") int proNo, @Param("memberType") int memberType);

	void deletePortfolioByProNo(@Param("proNo") int proNo);
	void deleteProfessionalAdvancedInfoByProNo(@Param("proNo") int proNo);

	void deleteProfessional(@Param("proNo") int proNo);



	// 게시물 비활성화 처리
	int disableTarget(@Param("type") String type, @Param("targetId") int targetId);

	void disableCommunity(@Param("communityBoardNo") int communityBoardNo);
	void disableCommunityReply(@Param("communityReplyNo") int communityReplyNo);
	void disableMember(@Param("memberNo") int memberNo);
	void disableReview(@Param("reviewNo") int reviewNo);

	// 신고 처리 상태 업데이트
	void updateReportsStatus(@Param("reportsNo") int reportsNo, @Param("status") int status);
}
