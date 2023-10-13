package spofo.portfolio.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spofo.global.domain.exception.PortfolioNotFound;
import spofo.portfolio.controller.port.PortfolioService;
import spofo.portfolio.domain.Portfolio;
import spofo.portfolio.domain.PortfolioCreate;
import spofo.portfolio.domain.PortfolioStatistic;
import spofo.portfolio.domain.PortfolioUpdate;
import spofo.portfolio.domain.TotalPortofoliosStatistic;
import spofo.portfolio.service.port.PortfolioRepository;
import spofo.stock.domain.Stock;
import spofo.stock.service.StockServerService;
import spofo.stockhave.domain.StockHave;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final StockServerService stockServerService;

    @Override
    public TotalPortofoliosStatistic getPortfoliosStatistic(Long memberId) {
        List<Portfolio> portfolios = portfolioRepository.findByMemberIdWithTradeLogs(memberId);
        List<PortfolioStatistic> portfolioStatistics = getPortfolioStatistics(portfolios);
        return TotalPortofoliosStatistic.of(portfolioStatistics);
    }

    @Override
    public List<PortfolioStatistic> getPortfolios(Long memberId) {
        List<Portfolio> portfolios = portfolioRepository.findByMemberIdWithTradeLogs(memberId);
        return getPortfolioStatistics(portfolios);
    }

    @Override
    public Portfolio getPortfolio(Long id) {
        return findById(id);
    }

    @Override
    public PortfolioStatistic getPortfolioStatistic(Long id) {
        Portfolio portfolio = getPortfolioFrom(portfolioRepository.findByIdWithTradeLogs(id));
        return getPortfolioStatistics(List.of(portfolio)).get(0);
    }

    @Override
    @Transactional
    public Portfolio create(PortfolioCreate request, Long memberId) {
        Portfolio portfolio = Portfolio.of(request, memberId);
        return portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional
    public Portfolio update(PortfolioUpdate request, Long id, Long memberId) {
        Portfolio savedPortfolio = findById(id);
        Portfolio updatedPortfolio = savedPortfolio.update(request, memberId);
        return portfolioRepository.save(updatedPortfolio);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Portfolio portfolio = findById(id);
        portfolioRepository.delete(portfolio);
    }

    private Portfolio findById(Long id) {
        return getPortfolioFrom(portfolioRepository.findById(id));
    }

    private Portfolio getPortfolioFrom(Optional<Portfolio> portfolioOptional) {
        return portfolioOptional.orElseThrow(() -> new PortfolioNotFound());
    }

    private List<PortfolioStatistic> getPortfolioStatistics(List<Portfolio> portfolios) {
        List<String> stockCodes = getStockCodes(portfolios);
        Map<String, Stock> stocks = stockServerService.getStocks(stockCodes);
        return portfolios.stream()
                .map(portfolio -> PortfolioStatistic.of(portfolio, stocks))
                .toList();
    }

    private List<String> getStockCodes(List<Portfolio> portfolios) {
        return portfolios.stream()
                .flatMap(portfolio -> portfolio.getStockHaves().stream()
                        .map(StockHave::getStockCode))
                .distinct()
                .toList();
    }
}
