package spofo.tradelog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import spofo.tradelog.enums.TradeType;

@Entity
@Table(name = "TRADE_LOG_TB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long stockId; // 보유 종목 번호 (FK)

    @Column(columnDefinition = "VARCHAR(1) DEFAULT 'B'", nullable = false)
    @Enumerated(EnumType.STRING)
    private TradeType type;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(updatable = false, nullable = false)
    private LocalDateTime tradeDate;

    @Column(precision = 30, scale = 15, nullable = false)
    private BigDecimal quantity;

    @Column(precision = 18, scale = 2, nullable = false)
    private BigDecimal marketPrice;

    @Column(updatable = false, nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}