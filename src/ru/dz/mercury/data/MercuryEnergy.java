package ru.dz.mercury.data;

import java.io.IOException;

import ru.dz.mercury.Mercury230Connection;
import ru.dz.mercury.Mercury230ProtocolException;
import ru.dz.mercury.pkt.EnergyReadRequestPacket;
import ru.dz.mercury.pkt.Packet;

public class MercuryEnergy 
{
	private double[] eTariff1 	= new double[4]; 
	private double[] eTariff2 	= new double[4]; 
	private double[] eTariff3 	= new double[4]; 
	private double[] eTariff4 	= new double[4]; 
	private double[] eTotal 	= new double[4]; 
	private double[] eLoss 		= new double[4]; 

	/**
	 * Request and decode energy counters.
	 * @throws IOException 
	 * @throws Mercury230ProtocolException 
	 */
	public MercuryEnergy(Mercury230Connection c) throws IOException, Mercury230ProtocolException
	{
		c.sendPacked(new EnergyReadRequestPacket(c.getNetAddress(),0,6));
		Packet pkt = c.readNonRcPacket();
		
		//if(pkt.isReturnCodePacket())			throw new Mercury230ProtocolException("Got rc="+pkt.getReturnCode());
		
		byte[] packet = pkt.getPayload();
		
		MercuryFixed.decode4x4(packet, 16*0, eTariff1);
		MercuryFixed.decode4x4(packet, 16*1, eTariff2);
		MercuryFixed.decode4x4(packet, 16*2, eTariff3);
		MercuryFixed.decode4x4(packet, 16*3, eTariff4);

		MercuryFixed.decode4x4(packet, 16*4, eTotal);
		MercuryFixed.decode4x4(packet, 16*5, eLoss);
	}
	
	public void dumpAll()
	{

		System.out.println("E = "+eTariff1[0]+" \t"+eTariff1[1]+" \t"+eTariff1[2]+" \t"+eTariff1[3]+" \t(T1)");
		System.out.println("E = "+eTariff2[0]+" \t"+eTariff2[1]+" \t"+eTariff2[2]+" \t"+eTariff2[3]+" \t(T2)");
		System.out.println("E = "+eTariff3[0]+" \t"+eTariff3[1]+" \t"+eTariff3[2]+" \t"+eTariff3[3]+" \t(T2)");
		System.out.println("E = "+eTariff4[0]+" \t"+eTariff4[1]+" \t"+eTariff4[2]+" \t"+eTariff4[3]+" \t(T2)");
		
		System.out.println("E = "+eTotal[0]+" \t"+eTotal[1]+" \t"+eTotal[2]+" \t"+eTotal[3]+" \t(Total)");
		System.out.println("E = "+eLoss[0]+" \t"+eLoss[1]+" \t"+eLoss[2]+" \t"+eLoss[3]+" \t(Loss)");
	}

	/**
	 * Dump forward energy counts
	 */
	public void dumpForward()
	{

		System.out.println("E = "+eTariff1[0]+" \t"+eTariff1[2]+" \t(T1)");
		System.out.println("E = "+eTariff2[0]+" \t"+eTariff2[2]+" \t(T2)");
		System.out.println("E = "+eTariff3[0]+" \t"+eTariff3[2]+" \t(T2)");
		System.out.println("E = "+eTariff4[0]+" \t"+eTariff4[2]+" \t(T2)");
		
		System.out.println("E = "+eTotal[0]+" \t"+eTotal[2]+" \t(Total)");
		System.out.println("E = "+eLoss[0]+" \t"+eLoss[2]+" \t(Loss)");
	}

	/**
	 * Dump forward active energy counts
	 */
	public void dumpForwardActive()
	{

		System.out.print("Energy T1="+eTariff1[0]);
		System.out.print(" T2="+eTariff2[0]);
		System.out.print(" T3="+eTariff3[0]);
		System.out.print(" T4="+eTariff4[0]);
		
		System.out.print(" Total="+eTotal[0]);
		System.out.println(" Loss="+eLoss[0]);
	}

	/**
	 * Return energy count for tariff 1.
	 * <p>
	 * @return Active direct, Active reverse, reactive direct, Reactive reverse.
	 */
	public double[] geteTariff1() {
		return eTariff1;
	}

	/**
	 * Return energy count for tariff 2.
	 * <p>
	 * @return Active direct, Active reverse, reactive direct, Reactive reverse.
	 */
	public double[] geteTariff2() {
		return eTariff2;
	}

	/**
	 * Return energy count for tariff 3.
	 * <p>
	 * @return Active direct, Active reverse, reactive direct, Reactive reverse.
	 */
	public double[] geteTariff3() {
		return eTariff3;
	}

	/**
	 * Return energy count for tariff 4.
	 * <p>
	 * @return Active direct, Active reverse, reactive direct, Reactive reverse.
	 */
	public double[] geteTariff4() {
		return eTariff4;
	}

	/**
	 * Return energy count for all tariffs in total.
	 * <p>
	 * @return Active direct, Active reverse, reactive direct, Reactive reverse.
	 */
	public double[] geteTotal() {
		return eTotal;
	}

	/**
	 * Return energy loss count.
	 * <p>
	 * @return Active direct, Active reverse, reactive direct, Reactive reverse.
	 */
	public double[] geteLoss() {
		return eLoss;
	}


	/**
	 * Return direct active energy count for all tariffs.
	 * <p>
	 * @return Active direct: double[5], T1-T4, total.
	 */
	public double[] getActiveDirect() 
	{
		double[] ret = new double[5];
		
		ret[0] = eTariff1[0];
		ret[1] = eTariff2[0];
		ret[2] = eTariff3[0];
		ret[3] = eTariff4[0];
		ret[4] = eTotal[0];
		
		return ret;
	}


}
