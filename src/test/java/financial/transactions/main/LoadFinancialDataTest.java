package financial.transactions.main;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import financial.transactions.model.Transaction;
import financial.transactions.samples.LoadFinancialData;

class LoadFinancialDataTest {

	static LoadFinancialData lfd = null;
	
	@BeforeAll
	static void setupTests() throws IOException {		
		lfd = new LoadFinancialData("SampleData.txt");
	}
	
	
	
	@Test
	void testEmptyLine() {
		Transaction trn = lfd.makeTransaction(""); // empty line
		assertEquals(null, trn);
	}

	@Test
	void testWrongOperation() {				
		Transaction trn = lfd.makeTransaction("foo;X;0.52;SAR;10 Jan 2016;11 Feb 2016;350;105.25");
		assertEquals(null, trn);
	}
	
	@Test
	void testInvalidDate() {				
		Transaction trn = lfd.makeTransaction("foo;S;0.52;SAR;10 XYZ 2016;11 Feb 2016;350;105.25");
		assertEquals(null, trn);
	}
	
	@Test
	void testValidLine() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);;
		Transaction trn = lfd.makeTransaction("foo;S;0.49;AED;01 Jan 2016;02 Jan 2016;200;162.25");
		assertEquals("foo", trn.getEntity());
		assertEquals('S', trn.getOperation());
		assertEquals(LocalDate.parse("01 Jan 2016", formatter), trn.getInstructionDate());
		
		// Working day difference
		assertEquals(LocalDate.parse("03 Jan 2016", formatter), trn.getSettlementDate());
		
		assertEquals(200, trn.getUnits());
		assertEquals(new BigDecimal("162.25"), trn.getUnitPrice());
	}
}
