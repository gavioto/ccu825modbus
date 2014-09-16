package ru.dz.ccu825.transport;




import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.msg.ReadWriteMultipleResponse;
/**
 * Modified j2mod class used to support CCU825 modbus connector.
 * <p> 
 * Class implementing a <tt>Read / Write Multiple Registers</tt> request. Special
 * edition for CCU825 style packet I/O over ModBus.
 *
 * @author dz
 */

public final class CCU825_ReadWriteMultipleRequest extends ModbusRequest {

	private final static int m_ReadReference = 0;
	private final static int m_WriteReference = 0;
	
	private int m_ReadCount;
	private int m_WriteCount;

	






	private byte[] recvData;
	private byte[] sendData;
	/**
	 * writeData -- output this Modbus message to dout.
	 */
	public void writeData(DataOutput dout) throws IOException {
		dout.write(getMessage());
	}
	/**
	 * readData -- read the values of the registers to be written, along with
	 * the reference and count for the registers to be read.
	 */
	public void readData(DataInput input) throws IOException {
		int rcv_ReadReference = input.readShort();
		int rcv_ReadCount = input.readShort();
		int rcv_WriteReference = input.readShort();
		int rcv_WriteCount = input.readUnsignedShort();
	
		if( (rcv_ReadCount != m_ReadCount) || (rcv_WriteCount!=m_WriteCount))
			System.err.println("fn23 readcnt="+rcv_ReadCount+" writecnt="+m_WriteCount);

		if( (rcv_ReadReference !=0) || (rcv_WriteReference!=0))
			System.err.println("fn23 readref="+rcv_ReadReference+" writeref="+rcv_WriteReference);
		
		
		int byteCount = input.readUnsignedByte();
		
		
		byte buffer[] = new byte[byteCount];
		input.readFully(buffer, 0, byteCount);

		recvData = buffer;
	}

	public byte[] getRecvData() { return recvData; }
	
	public void setSendData(byte[] sd) { sendData = sd; }
	
	/**
	 * getMessage -- return a prepared message.
	 */
	public byte[] getMessage() {
		//byte results[] = new byte[9 + sendData.length];
		byte results[] = new byte[9 + 2 * m_WriteCount];
		results[0] = (byte) (m_ReadReference >> 8);
		results[1] = (byte) (m_ReadReference & 0xFF);
		results[2] = (byte) (m_ReadCount >> 8);
		results[3] = (byte) (m_ReadCount & 0xFF);
		results[4] = (byte) (m_WriteReference >> 8);
		results[5] = (byte) (m_WriteReference & 0xFF);
		results[6] = (byte) (m_WriteCount >> 8);
		results[7] = (byte) (m_WriteCount & 0xFF);
		// TO DO is it correct ModBus? possibly odd byte count
		//results[8] = (byte) (sendData.length);
		results[8] = (byte) (m_WriteCount * 2);
		int offset = 9;
		for (int i = 0; i < sendData.length; i++) {
			results[offset++] = sendData[i];
		}
		return results;
	}
	/**
	 * Constructs a new <tt>Read/Write Multiple Registers Request</tt> instance.
	 */
	public CCU825_ReadWriteMultipleRequest(int readCount, int writeCount) {
		super();

		setFunctionCode(Modbus.READ_WRITE_MULTIPLE);
		/*
		 * There is no additional data in this request.
		 */
		setDataLength(9 + writeCount * 2);

		m_ReadCount = readCount;
		m_WriteCount = writeCount;
	}
	
	
	/**
	 * createResponse -- create an empty response for this request.
	 */
	public ModbusResponse getResponse() {
		ReadWriteMultipleResponse response = null;
		response = new ReadWriteMultipleResponse();
		/*
		 * Copy any header data from the request.
		 */
		response.setHeadless(isHeadless());
		if (!isHeadless()) {
			response.setTransactionID(getTransactionID());
			response.setProtocolID(getProtocolID());
		}
		/*
		 * Copy the unit ID and function code.
		 */
		response.setUnitID(getUnitID());
		//response.setFunctionCode(getFunctionCode());
		return response;
	}
	public ModbusResponse createResponse() {
		ReadWriteMultipleResponse response = null;
		/*
		InputRegister[] readRegs = null;
		Register[] writeRegs = null;
		// 1. get process image
		ProcessImage procimg = ModbusCoupler.getReference().getProcessImage();
		// 2. get input registers range
		try {
			readRegs = procimg.getRegisterRange(getReadReference(),
					getReadWordCount());
			InputRegister[] dummy = new InputRegister[readRegs.length];
			for (int i = 0; i < readRegs.length; i++)
				dummy[i] = new SimpleInputRegister(readRegs[i].getValue());
			readRegs = dummy;
			writeRegs = procimg.getRegisterRange(getWriteReference(),
					getWriteWordCount());
			for (int i = 0; i < writeRegs.length; i++)
				writeRegs[i].setValue(getRegister(i).getValue());
		} catch (IllegalAddressException e) {
			return createExceptionResponse(Modbus.ILLEGAL_ADDRESS_EXCEPTION);
		}
		response = (ReadWriteMultipleResponse) getResponse();
		response.setRegisters(readRegs);
		*/
		return response;
	}




} 