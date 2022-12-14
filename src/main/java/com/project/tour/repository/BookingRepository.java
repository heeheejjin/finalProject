package com.project.tour.repository;

import com.project.tour.domain.Member;
import com.project.tour.domain.Package;
import com.project.tour.domain.QnA;
import com.project.tour.domain.UserBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<UserBooking, Long> {

    Page<UserBooking> findAll(Pageable pageable);
    Optional<UserBooking> findById(Long id);

    //사용자 정보와 예약날짜를 가지고 bookingNum 가져오기
    Optional<UserBooking> findByMemberAndBookingDate(Member member, LocalDateTime bookingDate);

    //방금 예약한 번호 들고오기
    @Query(value = "SELECT max(id) FROM Pay")
    public Long maxPayNum();

    //마이페이지에 출력할 멤버의 예약 리스트
    Page<UserBooking> findByMember_Id(Long id, Pageable pageable);

    //리뷰페이지에서 사용할 결제완료된 예약정보 리스트 가져오기
    //마이페이지에서 사용할 예약내역정보 리스트 가져오기
    List<UserBooking> findByMember_IdAndBookingStatus(Long id, int status);

    //마이페이지에서 사용할 예약취소내역 리스트 가져오기
    Page<UserBooking> findByMember_IdAndBookingStatusIn(Long id, List<Integer> status,Pageable pageable);



    //상세페이지 쇼트리뷰에서 사용할 예약취소,결제완료된 예약정보 리스트 가져오기
    List<UserBooking> findByMember_IdAndBookingStatusAndApackage(Long id, int status, Package apackage);


    @Query(value = "select sum(u.booking_Total_Count) from user_Booking u where u.package_Num=:packageNum and u.departure = :departure and u.booking_Status = 1", nativeQuery = true)
    Optional<Integer> a (@Param("packageNum") Long packageNum, @Param("departure") String departure);


}
