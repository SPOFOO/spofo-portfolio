package spofo.portfolio.service;

import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import spofo.global.exception.ErrorCode;
import spofo.global.exception.PortfolioException;
import spofo.portfolio.dto.request.CreatePortfolioRequest;
import spofo.portfolio.dto.response.CreatePortfolioResponse;
import spofo.portfolio.dto.response.PortfolioResponse;
import spofo.portfolio.dto.response.PortfolioSimpleResponse;
import spofo.portfolio.dto.response.TotalPortfolioResponse;
import spofo.portfolio.entity.Portfolio;
import spofo.portfolio.repository.PortfolioRepository;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final RestClient restClient = RestClient.builder().build();
    //private final StockHave stockHave;


    public String getStock() {
        return restClient.get()
                .uri("https://www.stock.spofo.net/1")
                .retrieve()
                .body(String.class);
    }

    public String getAuth() {
        return restClient.get()
                .uri("https://www.auth.spofo.net")
                .retrieve()
                .body(String.class);
    }

    public Long getMemberId() {
        return Long.valueOf(Objects.requireNonNull(restClient.get()
                .uri("https://www.auth.spofo.net:8080/auth/members/search")
                .retrieve()
                .body(String.class)));
    }

    // 전체 포트폴리오 자산 조회 api-001
    public TotalPortfolioResponse getTotalPortfolio(Long memberId) {
        List<Portfolio> portfolios = portfolioRepository.findByMemberId(memberId);
        BigDecimal totalAsset = getAllTotalAsset();
        BigDecimal gain = getAllGain();
        BigDecimal gainRate = getAllGainRate(getAllTotalAsset(), getAllBuy());
        BigDecimal dailyGainRate = getAllDailyGainRate();
        return TotalPortfolioResponse.from(totalAsset, gain, gainRate, dailyGainRate);
    }

    // todo: 전체 포폴 총 자산 계산 [목록 조회 총 자산의 합]
    private BigDecimal getAllTotalAsset() {
        return BigDecimal.ZERO;
    }

    // todo: 전체 포폴 평가 수익 [목록 조회 평가 수익금의 합]
    private BigDecimal getAllGain() {
        return BigDecimal.ZERO;
    }

    // todo: 전체 포폴 총 매수 금액 [목록 조회 총 매수 금액의 합]
    private BigDecimal getAllBuy() {
        return BigDecimal.ZERO;
    }

    // todo: 전체 포폴 수익률 [((총자산/총매수금액)*100)-100]
    private BigDecimal getAllGainRate(BigDecimal totalAsset, BigDecimal allBuy) {
        return totalAsset.divide(allBuy, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).subtract(
                        BigDecimal.valueOf(100));
    }

    // todo: 전체 포폴 일간 수익률(전날 대비 일간 수익률) [((오늘 총자산 - 어제 총자산)*100)-100]
    private BigDecimal getAllDailyGainRate() {
        return BigDecimal.ZERO;
    }

    // 포트폴리오 목록 조회 api-002
    public List<PortfolioSimpleResponse> getListPortfolio(Long memberId) {
        return portfolioRepository.findByMemberId(memberId).stream()
                .map(portfolio -> PortfolioSimpleResponse.from(portfolio,
                        getGain(getTotalAsset(), getTotalBuy()),
                        getGainRate(getTotalAsset(), getTotalBuy()))
                )
                .collect(toList());
    }

    // 포트폴리오 생성 api-005
    public CreatePortfolioResponse createPortfolio(CreatePortfolioRequest createPortfolioRequest) {
        Portfolio portfolio = createPortfolioRequest.toEntity();
        portfolioRepository.save(portfolio);
        return CreatePortfolioResponse.from(portfolio);
    }


    public PortfolioResponse getPortfolio(Long portfolioId) {
        Portfolio portfolio = findById(portfolioId);

        BigDecimal totalAsset = getTotalAsset();
        BigDecimal totalBuy = getTotalBuy();
        BigDecimal gain = getGain(totalAsset, totalBuy);
        BigDecimal gainRate = getGainRate(totalAsset, totalBuy);
        return PortfolioResponse.from(portfolio, totalAsset, totalBuy, gain, gainRate);
    }

    private Portfolio findById(Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() ->
                        new PortfolioException(ErrorCode.PORTFOLIO_NOT_FOUND));
    }

    /**
     * TODO : 총 자산 계산
     * 각 종목 현재가 * 수량
     **/
    private BigDecimal getTotalAsset() {
        return BigDecimal.ZERO;
    }

    /**
     * TODO : 총 매수 금액 계산
     * 각 종목의 구매가 * 수량
     **/
    private BigDecimal getTotalBuy() {
        return BigDecimal.ZERO;
    }

    /**
     * 평가 손익 계산 총 자산 - 총 매수 금액
     **/
    private BigDecimal getGain(BigDecimal totalAsset, BigDecimal totalBuy) {
        return totalAsset.subtract(totalBuy);
    }

    /**
     * 수익률 계산 ((총 자산/총 매수 금액)*100)-100
     **/
    private BigDecimal getGainRate(BigDecimal totalAsset, BigDecimal totalBuy) {
        return totalAsset.divide(totalBuy, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).subtract(
                        BigDecimal.valueOf(100));
    }

    /**
     * TODO : 일간 수익률 계산
     * private BigDecimal getReturnPerDay() {
     * <p>
     * }
     **/


}