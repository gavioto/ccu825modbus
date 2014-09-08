package ru.dz.ccu825.test;

/***
 * Java Modbus Library (jamod)
 * Copyright (c) 2002-2004, jamod development team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ***/
/***
 * Java Modbus Library (j2mod)
 * Copyright 2012-2014, Julianne Frances Haugh
 * d/b/a greenHouse Gas and Electric
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS ``AS
 * IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ***/

import java.io.IOException;
import java.util.Arrays;

import ru.dz.ccu825.CCU825Test;
import ru.dz.ccu825.transport.CCU825_ReadWriteMultipleRequest;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusRTUTransport;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransport;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransport;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransport;
import com.ghgande.j2mod.modbus.msg.ExceptionResponse;
import com.ghgande.j2mod.modbus.msg.ModbusRequest;
import com.ghgande.j2mod.modbus.msg.ModbusResponse;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.ReadWriteMultipleRequest;
import com.ghgande.j2mod.modbus.msg.ReadWriteMultipleResponse;
import com.ghgande.j2mod.modbus.net.ModbusMasterFactory;
import com.ghgande.j2mod.modbus.procimg.AbstractRegister;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
/**
 * Class that implements a simple command line tool for writing to an analog
 * output over a Modbus/TCP connection.
 *
 * <p>
 * Note that if you write to a remote I/O with a Modbus protocol stack, it will
 * most likely expect that the communication is <i>kept alive</i> after the
 * first write message.
 *
 * <p>
 * This can be achieved either by sending any kind of message, or by repeating
 * the write message within a given period of time.
 *
 * <p>
 * If the time period is exceeded, then the device might react by turning off
 * all signals of the I/O modules. After this timeout, the device might require
 * a reset message.
 *
 * @author Dieter Wimberger
 * @version 1.2rc1 (09/11/2004)
 *
 * @author Julie Haugh
 * @version 1.03 (1/18/2014)
 */
public class J2Mod_read_holding_test {


	public static void main(String[] args_off) {
		ModbusTransport transport = null;

		try {
			try {
				// 2. Open the connection.
				transport = ModbusMasterFactory.createModbusMaster("tcp:localhost:502");
				if (transport == null) {
					System.err.println("Cannot open TCP");
					System.exit(1);
				}
				if (transport instanceof ModbusSerialTransport) {
					((ModbusSerialTransport) transport).setReceiveTimeout(500);
					if (System.getProperty("com.ghgande.j2mod.modbus.baud") != null)
						((ModbusSerialTransport) transport).setBaudRate(Integer.parseInt(System.getProperty("com.ghgande.j2mod.modbus.baud")));
					else
						((ModbusSerialTransport) transport).setBaudRate(19200);
				}
				/*
				 * There are a number of devices which won't initialize immediately
				 * after being opened. Take a moment to let them come up.
				 */
				//Thread.sleep(2000);


				if (transport instanceof ModbusTCPTransport) {
					//unit = 0;
				} else if (transport instanceof ModbusRTUTransport) {
					System.err.println("Just TCP!");
					System.exit(1);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(1);
			}

			//doReadHolding(transport,10);
			
			//do23(transport);
			
			do23_CCU825(transport);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			// 6. Close the connection
			if (transport != null)
				transport.close();
		} catch (IOException e) {
			// Do nothing.
		}
		System.exit(0);
	}

	private static void doReadHolding( ModbusTransport transport, int repeat) 
	{
		ModbusTransaction trans = null;
		ModbusRequest req = null;
		int ref = 0;
		int count = 0;
		int unit = 0;

		ref = 0;
		count = 2;
		repeat = 1;		

		// 3. Create the command.
		req = new ReadMultipleRegistersRequest(ref, count);
		req.setUnitID(unit);
		// 4. Prepare the transaction
		trans = transport.createTransaction();
		trans.setRequest(req);
		req.setHeadless(trans instanceof ModbusSerialTransaction);
		if (Modbus.debug)
			System.out.println("Request: " + req.getHexMessage());

		// 5. Execute the transaction repeat times
		for (int i = 0; i < repeat; i++) {
			try {
				trans.execute();
			} catch (ModbusException x) {
				System.err.println(x.getMessage());
				continue;
			}
			ModbusResponse res = trans.getResponse();
			if (Modbus.debug) {
				if (res != null)
					System.out.println("Response: " + res.getHexMessage());
				else
					System.err.println("No response to READ HOLDING request.");
			}
			if (res instanceof ExceptionResponse) {
				ExceptionResponse exception = (ExceptionResponse) res;
				System.out.println(exception);
				continue;
			}
			if (! (res instanceof ReadMultipleRegistersResponse))
				continue;
			ReadMultipleRegistersResponse data = (ReadMultipleRegistersResponse) res;
			Register values[] = data.getRegisters();
			System.out.println("Data: " + Arrays.toString(values));
		}
	}



	private static void do23( ModbusTransport transport ) 
	{		

		int unit = 0;


		// 3. Create the command.
		//req = new ReadMultipleRegistersRequest(ref, count);
		ReadWriteMultipleRequest req = new ReadWriteMultipleRequest(unit, 0, 10, 0, 2);
		
		req.setUnitID(unit);

		Register[] registers = new Register[2];
		registers[0] = new AbstractRegister() {
			@Override
			public int getValue() { return 0x10; }
		};
		registers[1] = new AbstractRegister() {
			@Override
			public int getValue() { return 0x20; }
		};
		req.setRegisters(registers);
		
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
			System.err.println(x.getMessage());
			return;
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
			System.out.println(exception);
			return;
		}
		
		//if (! (res instanceof ReadMultipleRegistersResponse))			return;
		
		ReadWriteMultipleResponse data = (ReadWriteMultipleResponse) res;
		InputRegister[] values = data.getRegisters();
		System.out.println("Data: " + Arrays.toString(values));

	}


	private static void do23_CCU825( ModbusTransport transport ) 
	{		

		byte[] sd = { 1, 2, 3, 4, 5, 6 };
		// 3. Create the command.
		//req = new ReadMultipleRegistersRequest(ref, count);
		
		// TODO recreate write count from send data size
		CCU825_ReadWriteMultipleRequest req = new CCU825_ReadWriteMultipleRequest(2, sd.length/2 );
		
		req.setUnitID(0);

		
		req.setSendData(sd);
		
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
			System.err.println(x.getMessage());
			return;
		}

		/*
		byte[] recvData = req.getRecvData();
		
		CCU825Test.dumpBytes("recv", recvData);
		*/
		
		
		ModbusResponse res = trans.getResponse();
		
		if (Modbus.debug) {
			if (res != null)
				System.out.println("Response: " + res.getHexMessage());
			else
				System.err.println("No response to ReadWriteMultiple request.");
		}
		
		if (res instanceof ExceptionResponse) {
			ExceptionResponse exception = (ExceptionResponse) res;
			System.out.println(exception);
			return;
		}
		
		//if (! (res instanceof ReadMultipleRegistersResponse))			return;
		
		ReadWriteMultipleResponse data = (ReadWriteMultipleResponse) res;
		
		byte[] recvData = data.getMessage();
		CCU825Test.dumpBytes("recv", recvData);
		
		/*
		InputRegister[] values = data.getRegisters();
		System.out.println("Data: " + Arrays.toString(values));
		*/
	}
	
	

} 