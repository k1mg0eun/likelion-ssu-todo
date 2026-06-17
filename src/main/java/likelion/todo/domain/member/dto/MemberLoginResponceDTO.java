package likelion.todo.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MemberLoginResponceDTO(
        @JsonProperty("member_id")
        Long memberId
) {

}
