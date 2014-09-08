package ru.dz.ccu825.transport;

import java.io.IOException;

import ru.dz.ccu825.CCU825Test;
import ru.dz.ccu825.util.CCU825Exception;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransport;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransport;
import com.ghgande.j2mod.modbus.msg.ExceptionResponse;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.msg.ReadWriteMultipleResponse;
import com.ghgande.j2mod.modbus.net.ModbusMasterFactory;

public class CCU825_j2mod_connector implements ModBusConnection {
	private ModbusTransport transport = null;
	private String dest = "tcp:localhost:502";
	private int baud = 115200;

	@Override
	public void setSpeed(int baud) {
		// ignore for tcp
	}

	public void setDestination(String dest) {
		this.dest = dest;
	}


	@Override
	public void connect() throws CCU825Exception {


		try {
			// 2. Open the connection.
			transport = ModbusMasterFactory.createModbusMaster(dest);
			if (transport == null)
				throw new CCU825Exception("Cannot open TCP");


			if (transport instanceof ModbusSerialTransport) {
				((ModbusSerialTransport) transport).setReceiveTimeout(500);
				((ModbusSerialTransport) transport).setBaudRate(baud);
			}
			/*
			 * There are a number of devices which won't initialize immediately
			 * after being opened. Take a moment to let them come up.
			 */
			//Thread.sleep(2000);


			if (transport instanceof ModbusTCPTransport) {
				//unit = 0;
			} else if (transport instanceof ModbusRTUTransport) {
				throw new CCU825Exception("Just TCP yet");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CCU825Exception(ex);
		}


	}

	@Override
	public void disconnect() {
		try {

			if (transport != null)
				transport.close();
		} catch (IOException e) {
			e.printStackTrace();
			// Do nothing.
		}
	}

	@Override
	public byte[] rwMultiple(int nRead, byte[] writeData) throws CCU825Exception {

		
		// TODO recreate write count from send data size
		CCU825_ReadWriteMultipleRequest req = new CCU825_ReadWriteMultipleRequest( nRead, writeData.length/2 );
		
		req.setUnitID(0);	
		req.setSendData(writeData);
		
		// 4. Prepare the transaction
		ModbusTransaction trans = transport.createTransaction();
		
		trans.setRequest(req);
		
		req.setHeadless(trans instanceof ModbusSerialTransaction);
		
		if (Modbus.debug)
			System.out.println("Request: " + req.getHexMessage());

		// 5. Execute the transaction repeat times

		try {
			trans.execute();
		} catch (ModbusException x) {			
			throw new CCU825Exception(x);
		}


		ModbusResponse res = trans.getResponse();
		
		if (Modbus.debug) {
			if (res != null)
				System.out.println("Response: " + res.getHexMessage());
			else
				System.err.println("No response to ReadWriteMultiple request.");
		}
		
		if (res instanceof ExceptionResponse) {
			ExceptionResponse exception = (ExceptionResponse) res;
			throw new CCU825Exception(exception.toString());
		}
		
		//if (! (res instanceof ReadMultipleRegistersResponse))			return;
		
		ReadWriteMultipleResponse data = (ReadWriteMultipleResponse) res;
		
		byte[] recvData = data.getMessage();
		CCU825Test.dumpBytes("recv", recvData);
		
		assert( recvData.length == nRead*2+1 );
		
		byte[] justRegs = new byte[nRead*2]; 

		// remove 1st byte - contains len of array
		System.arraycopy(recvData, 1, justRegs, 0, nRead*2);
		
		return justRegs;
		/*
		InputRegister[] values = data.getRegisters();
		System.out.println("Data: " + Arrays.toString(values));
		*/

	}



}
