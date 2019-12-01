package financial.transactions.model;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class Transaction {

	public static final char buy = 'B';
	public static final char sell = 'S';

	private String entity;

	// assumes two values 'B': Buy, 'S': Sell
	private char operation;

	private BigDecimal agreedFx;

	private String currency;

	private LocalDate instructionDate;

	private LocalDate settlementDate;

	// number of shares to be bought or sold
	private long units;

	private BigDecimal unitPrice;

	public Transaction(String entity, char operation, BigDecimal agreedFx, String currency, LocalDate instructionDate,
			LocalDate settlementDate, long units, BigDecimal unitPrice) {
		super();
		this.entity = entity;
		this.operation = operation;
		this.agreedFx = agreedFx;
		this.currency = currency;
		this.instructionDate = instructionDate;
		this.settlementDate = settlementDate;
		this.units = units;
		this.unitPrice = unitPrice;

		if (operation != buy && operation != sell) {
			throw new IllegalArgumentException();
		}

		DayOfWeek dayOfWeek = settlementDate.getDayOfWeek();

		// hard coded, it would be better to keep a list of currencies and their working
		// days for currencies that don't work from Monday to Friday
		if (currency.equals("AED") || currency.equals("SAR")) {

			switch (dayOfWeek) {
			case FRIDAY:
				this.settlementDate = settlementDate.plusDays(2);
				break;
			case SATURDAY:
				this.settlementDate = settlementDate.plusDays(1);
				break;
			default:
				break;
			}
		} else { // all other currencies
			switch (dayOfWeek) {
			case SATURDAY:
				this.settlementDate = settlementDate.plusDays(2);
				break;
			case SUNDAY:
				this.settlementDate = settlementDate.plusDays(1);
				break;
			default:
				break;
			}
		}
		// many other validations could have been written
	}

	public String getEntity() {
		return entity;
	}

	public char getOperation() {
		return operation;
	}

	public BigDecimal getAgreedFx() {
		return agreedFx;
	}

	public String getCurrency() {
		return currency;
	}

	public LocalDate getInstructionDate() {
		return instructionDate;
	}

	public LocalDate getSettlementDate() {
		return settlementDate;
	}

	public long getUnits() {
		return units;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	@Override
	public String toString() {
		return "Transaction [entity=" + entity + ", operation=" + operation + ", agreedFx=" + agreedFx + ", currency="
				+ currency + ", instructionDate=" + instructionDate + ", settlementDate=" + settlementDate + ", units="
				+ units + ", unitPrice=" + unitPrice + "]";
	}

}
