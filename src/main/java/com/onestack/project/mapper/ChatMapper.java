package com.onestack.project.mapper;

import com.onestack.project.domain.ChatMessage;
import com.onestack.project.domain.ChatRoom;
import com.onestack.project.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMapper {
    // 채팅방 생성
    void createChatRoom(ChatRoom room);
    
    // 채팅방 조회
    ChatRoom getChatRoomById(@Param("roomId") String roomId);
    
    // 채팅 메시지 삽입
    void insertMessage(ChatMessage message);
    
    // 채팅방 최근 메시지 조회
    List<ChatMessage> getRecentMessages(@Param("roomId") String roomId, @Param("limit") int limit);
    
    // 채팅방 삭제
    void deleteChatRoom(@Param("roomId") String roomId);
    
    // 채팅방의 모든 메시지 삭제
    void deleteAllMessages(String roomId);
    
    // 채팅방 참여자 수 업데이트
    void updateParticipantCount(@Param("roomId") String roomId, @Param("count") int count);
    
    // 채팅방 비밀번호 조회
    String getRoomPassword(@Param("roomId") String roomId);
    
    // 참여자 추가
    void addParticipant(@Param("roomId") String roomId, @Param("memberId") String memberId);
    
    // 참여자 제거
    void removeParticipant(@Param("roomId") String roomId, @Param("memberId") String memberId);
    
    // 참여자 확인
    boolean isParticipant(@Param("roomId") String roomId, @Param("memberId") String memberId);
    
    // 참여자 수 조회
    int getParticipantCount(@Param("roomId") String roomId);
    
    // 채팅방 권한 확인
    boolean isRoomAdmin(@Param("roomId") String roomId, @Param("memberId") String memberId);
    
    // 채팅방 권한 양도
    void transferRoomAdmin(@Param("roomId") String roomId, 
                          @Param("currentAdmin") String currentAdmin,
                          @Param("newAdmin") String newAdmin);
    
    // 채팅방 참여자 목록 조회
    List<Member> getRoomParticipants(@Param("roomId") String roomId);
    
    // 채팅방의 모든 참여자 삭제
    void deleteAllParticipants(String roomId);

    // 사용자가 참여중인 채팅방 목록 조회
    List<ChatRoom> getMyChatRooms(@Param("memberNo") int memberNo);
} 