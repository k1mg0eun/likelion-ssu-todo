package likelion.todo.domain.todo.entity;

import jakarta.persistence.*;
import likelion.todo.domain.global.entity.BaseEntity;
import likelion.todo.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private LocalDateTime date;

    @Column(name = "is_checked")
    private boolean isChecked;

    @Column(length = 20)
    private String emoji;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Todo(String content, LocalDateTime date, boolean isChecked, String emoji, Member member){
        this.content = content;
        this.date = date;
        this.isChecked = isChecked;
        this.emoji = emoji;
        this.member = member;
    }

    // 내용/날짜 수정 (null이면 기존 값 유지 = 부분 수정 가능)
    public void update(String content, LocalDateTime date) {
        if (content != null) {
            this.content = content;
        }
        if (date != null) {
            this.date = date;
        }
    }

    // 완료 토글 (true ↔ false 뒤집기)
    public void complete() {
        this.isChecked = !this.isChecked;
    }

    // 이모지(회고) 변경
    public void updateEmoji(String emoji) {
        this.emoji = emoji;
    }

}
