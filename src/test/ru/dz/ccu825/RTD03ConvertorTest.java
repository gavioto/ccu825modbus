package test.ru.dz.ccu825;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.dz.ccu825.convert.RTD03Convertor;

public class RTD03ConvertorTest {

	
	@Test
	public void testConvert() {
		RTD03Convertor c = new RTD03Convertor();
		
		assertEquals(c.convert(2.5), 0.0, 0.001);
	}

	@Test
	public void testConvertBack() {
		RTD03Convertor c = new RTD03Convertor();
		
		assertEquals(c.convertBack(0), 2.5, 0.001);
	}

}
