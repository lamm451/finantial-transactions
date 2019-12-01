package financial.transactions.main;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import financial.transactions.model.EntitiesRanking;
import financial.transactions.model.Transaction;
import financial.transactions.samples.LoadFinancialData;

public class ProcessTransactions {

	private static final Logger LOGGER = Logger.getLogger(ProcessTransactions.class.getName());
	
	private DecimalFormat formatter = new DecimalFormat("###,##0.00");

	public static void main(String[] args) {

		try {
			LoadFinancialData loadFinancialData = new LoadFinancialData("SampleData.txt");
			List<Transaction> listTransactions = loadFinancialData.load();
			ProcessTransactions processor = new ProcessTransactions();
			processor.processReport(listTransactions);

		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
			// e.printStackTrace(); Dangerous: gives valuable internal class info to a
			// hacker
			System.err.println("Error reading data file");
			System.exit(1);
		}
	}

	private void processReport(List<Transaction> listTransactions) {

		listTransactions.sort((x, y) -> x.getSettlementDate().compareTo(y.getSettlementDate()));

		Iterator<Transaction> iterator = listTransactions.iterator();

		if (!iterator.hasNext()) {
			LOGGER.log(Level.INFO, "No transactions to report, " + LocalDate.now());
			return;
		}
		
		boolean hasMoved = false;
		Transaction trn = null;
		
		// Keep track of entities
		List <EntitiesRanking> entities = new ArrayList<>();

		do {			
			BigDecimal buyOutgoing = BigDecimal.valueOf(0.0).setScale(2, RoundingMode.HALF_UP);

			BigDecimal sellIncoming = BigDecimal.valueOf(0.0).setScale(2, RoundingMode.HALF_UP);

			if (!hasMoved) {
				trn = iterator.next();
				hasMoved = true;
			}

			LocalDate processingDate = trn.getSettlementDate();
			
			BigDecimal parcel = amountOfTrade(trn);

			if (trn.getOperation() == Transaction.sell) {
				sellIncoming = sellIncoming.add(parcel);
			} else {
				buyOutgoing = buyOutgoing.add(parcel);
			}
			
			rankEntities(trn.getEntity(), trn.getOperation(), parcel, entities);

			if (iterator.hasNext()) {
				trn = iterator.next();
				hasMoved = true;
				
				while (trn.getSettlementDate().compareTo(processingDate) == 0) {
					parcel = amountOfTrade(trn);
					if (trn.getOperation() == Transaction.sell) {
						sellIncoming = sellIncoming.add(parcel);
					} else {
						buyOutgoing = buyOutgoing.add(parcel);
					}
					rankEntities(trn.getEntity(), trn.getOperation(), parcel, entities);
					
					if (iterator.hasNext()) {
						trn = iterator.next();
						hasMoved = true;
					} else {
						hasMoved = false;
						break;
					}
				}
			} else {
				hasMoved = false;
			}
			
			System.out.println(processingDate + " -> Incoming: $" + 
					formatter.format(sellIncoming.setScale(2, RoundingMode.HALF_UP)) + 
					 " Outgoing: $" + 
					 formatter.format(buyOutgoing.setScale(2, RoundingMode.HALF_UP)));
			
		} while (iterator.hasNext() || hasMoved);
		
		printEntitiesRanking(entities);		
	}

	private void printEntitiesRanking(List<EntitiesRanking> entities) {
		
		System.out.println("Ranking by incoming:");
		entities.stream()
		.sorted((x, y) -> y.getSellIncoming().compareTo(x.getSellIncoming()))
		.forEach(x -> System.out.println(x.getEntity() + ": $" + formatter.format(x.getSellIncoming())));
		
		
		System.out.println("Ranking by outgoing:");
		entities.stream()
		.sorted((x, y) -> y.getBuyOutgoing().compareTo(x.getBuyOutgoing()))
		.forEach(x -> System.out.println(x.getEntity() + ": $" + formatter.format(x.getBuyOutgoing())));
		
	}

	private int getIndex(final String entity, final List<EntitiesRanking> entities) {
		int index;
		for (index = 0; index < entities.size(); ++index) {
			if (entity.equals(entities.get(index).getEntity())) {
				return index;
			}
		}
		return -1; // signal not found
	}

	private void rankEntities(final String entity, final char operation, final BigDecimal parcel, 
			List<EntitiesRanking> entities) {
		
		EntitiesRanking entitiesRanking;
		
		int index = getIndex(entity, entities);
		
		if (index != -1) {
			entitiesRanking = entities.get(index);
			if (operation == Transaction.sell) {
				entitiesRanking.setSellIncoming(entitiesRanking.getSellIncoming().add(parcel));
			} else {
				entitiesRanking.setBuyOutgoing(entitiesRanking.getBuyOutgoing().add(parcel));
			}
			entities.set(index, entitiesRanking);
		} else {
			entitiesRanking = new EntitiesRanking();
			entitiesRanking.setEntity(entity);
			if (operation == Transaction.sell) {
				entitiesRanking.setSellIncoming(parcel);
				entitiesRanking.setBuyOutgoing(BigDecimal.valueOf(0.0));
			} else {
				entitiesRanking.setSellIncoming(BigDecimal.valueOf(0.0));
				entitiesRanking.setBuyOutgoing(parcel);
			}
			entities.add(entitiesRanking);
		}
		
	}

	private BigDecimal amountOfTrade(final Transaction trn) {
		// price_per_unit * Units * agreedFix
		return trn.getUnitPrice().multiply(new BigDecimal(trn.getUnits())).multiply(trn.getAgreedFx());
	}	
	
}
