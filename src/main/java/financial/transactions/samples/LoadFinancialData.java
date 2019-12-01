package financial.transactions.samples;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import financial.transactions.model.Transaction;

public class LoadFinancialData {
	
	private static final Logger LOGGER = Logger.getLogger(LoadFinancialData.class.getName());
	private String filename;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);;
	
	// "SampleData.txt" is the name I use for the financial data
	// it must be present on the folder where is POM.XML
	// or on the same folder as the executable jar
	
	public LoadFinancialData() {		
	}
	
	public LoadFinancialData(String dataFile) throws IOException {
		filename = new java.io.File(".").getCanonicalPath() + 
				File.separator + dataFile;		
	}
	
	public List<Transaction> load() throws IOException {
		List<Transaction> listTransactions = null;
		try (Stream<String> stream = Files.lines(Paths.get(filename))) {
			listTransactions = stream.map(this::makeTransaction).collect(Collectors.toList());
			listTransactions =
			listTransactions.stream().filter(Objects::nonNull).collect(Collectors.toList());
		}
		if (listTransactions == null) {
			listTransactions = new ArrayList<>();
		}
		return listTransactions;
	}
	
	/**
	 * 
	 * @param line
	 * @return
	 * Assumes a 2 digit precision for the decimals
	 */
	public Transaction makeTransaction(String line) {		
		StringTokenizer st = new StringTokenizer(line, ";");
		Transaction t = null;
		try {
			t = new Transaction(
				st.nextToken(),
				st.nextToken().charAt(0),
				new BigDecimal(st.nextToken()).setScale(2, RoundingMode.HALF_UP),
				st.nextToken(),
				LocalDate.parse(st.nextToken(), formatter),
				LocalDate.parse(st.nextToken(), formatter),
				Long.parseLong(st.nextToken()),
				new BigDecimal(st.nextToken()).setScale(2, RoundingMode.HALF_UP) 
			);			
		} catch (Exception ex) {
			//LOGGER.log(Level.SEVERE, "Invalid financial data: " + ex.getMessage(), ex);
			LOGGER.log(Level.SEVERE, "Invalid financial data: ");
			//System.err.println("Invalid financial data");
			return null;
			//System.exit(1);
		}
		return t;
	}	
}
