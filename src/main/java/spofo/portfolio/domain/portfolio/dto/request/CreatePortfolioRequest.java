package spofo.portfolio.domain.portfolio.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spofo.portfolio.domain.portfolio.entity.Portfolio;
import spofo.portfolio.domain.portfolio.enums.PortfolioType;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePortfolioRequest {

    private String name;
    private String detail;
    private String currency;
    private PortfolioType type;

    public Portfolio toEntity() {
        return Portfolio.builder()
                .name(name)
                .detail(detail)
                .currency(currency)
                .type(type)
                .build();
    }


}
