package likelion.todo.domain.todo.repository;

import likelion.todo.domain.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // 메서드 이름만으로 쿼리 자동 생성: member의 id로 찾아서 date 오름차순 정렬
    List<Todo> findAllByMemberIdOrderByDateAsc(Long memberId);

    // 복잡한 조건(월/일)은 직접 JPQL로 작성
    @Query("""
            select t
            from Todo t
            where t.member.id = :memberId
            and month(t.date) = :month
            and day(t.date) = :day
            order by t.date asc
            """)
    List<Todo> findAllByMemberIdAndMonthAndDay(
            @Param("memberId") Long memberId,
            @Param("month") Integer month,
            @Param("day") Integer day
    );
}