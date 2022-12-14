package com.project.tour.domain;

import com.project.tour.oauth.model.BaseAuthRole;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private String name;

    @Column
    private String birth;

    @Column(name = "phone_num")
    private String phone;

    @Column
    private int point;

    private String coupons;

    private LocalDateTime createdDate;

    @Column
    private String keyword;
    private String social;


    public void updatePassword(String password){
        this.password = password;
    }

    @Builder            //소셜에서 넘어오는 데이터 받아주는 곳
    public Member(String name, String email,String social) {

        this.name=name;
        this.email=email;
        this.social=social;

    }

    public void updateSocailInfo(String email,String name){
        this.name = name;
        this.email = email;
    }

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<EstimateInquiry>  estimateInquiries;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<NoticeRecommend> noticeRecommends;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<NoticeReply> noticeReplies;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<Pay> pays;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<QnA> qnAS;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<ReviewLike> reviewLikes;

    @OneToMany(mappedBy = "author", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<Review> reviews;

    @OneToMany(mappedBy = "userName", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<ShortReview> shortReviews;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<UserBooking> userBookings;

    @OneToMany(mappedBy = "author", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<VoiceCus> voiceCuses;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.ALL}, orphanRemoval=true)
    private List<WishList> wishLists;







}
