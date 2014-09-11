package test.ru.dz.ccu825;

import static org.junit.Assert.*;

import org.junit.Test;

import ru.dz.ccu825.convert.RTD03Convertor;

public class RTD03ConvertorTest {

	
	@Test
	public void testConvert() {
		RTD03Convertor c = new RTD03Convertor();
		
		assertEquals(c.convert(2.5), 0.0, 0.001);
		assertEquals(c.convert(0), -50.0, 0.001);
		assertEquals(c.convert(10), 150.0, 0.001);
		
		c.settOffset(1);
		assertEquals(c.convert(2.5), 1.0, 0.001);

		c.settOffset(0);
		c.settMult(2);
		assertEquals(c.convert(2.5), 0.0, 0.001);
		assertEquals(c.convert(0), -100.0, 0.001);
		assertEquals(c.convert(10), 300.0, 0.001);
		
		c.settOffset(1);
		c.settMult(2);
		assertEquals(c.convert(2.5), 1.0, 0.001);
		assertEquals(c.convert(0), -99.0, 0.001);
		assertEquals(c.convert(10), 301.0, 0.001);
		
	}

	@Test
	public void testConvertBack() {
		RTD03Convertor c = new RTD03Convertor();
		
		assertEquals(c.convertBack(0.0), 2.5, 0.001);
		assertEquals(c.convertBack(-50), 0, 0.001);
		assertEquals(c.convertBack(150), 10.0, 0.001);

		c.settOffset(1);
		assertEquals(c.convertBack(1.0), 2.5, 0.001);

		c.settOffset(0);
		c.settMult(2);
		assertEquals(c.convertBack(0.0), 2.5, 0.001);
		assertEquals(c.convertBack(-100), 0, 0.001);
		assertEquals(c.convertBack(300), 10.0, 0.001);

		c.settOffset(-1);
		c.settMult(2);
		assertEquals(c.convertBack(-1.0), 2.5, 0.001);
		assertEquals(c.convertBack(-101), 0, 0.001);
		assertEquals(c.convertBack(299), 10.0, 0.001);
	}

}
