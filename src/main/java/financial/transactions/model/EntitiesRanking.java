package financial.transactions.model;

import java.math.BigDecimal;

public class EntitiesRanking {

	private String entity;
	private BigDecimal sellIncoming;
	private BigDecimal buyOutgoing;
	
	public EntitiesRanking() {
		// NullPointerException if not properly initialized
		sellIncoming = buyOutgoing = null;
		entity = null;
	}

	public EntitiesRanking(String entity, BigDecimal sellIncoming, BigDecimal buyOutgoing) {		
		this.entity = entity;
		this.sellIncoming = sellIncoming;
		this.buyOutgoing = buyOutgoing;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public BigDecimal getSellIncoming() {
		return sellIncoming;
	}

	public void setSellIncoming(BigDecimal sellIncoming) {
		this.sellIncoming = sellIncoming;
	}

	public BigDecimal getBuyOutgoing() {
		return buyOutgoing;
	}

	public void setBuyOutgoing(BigDecimal buyOutgoing) {
		this.buyOutgoing = buyOutgoing;
	}
		
}
