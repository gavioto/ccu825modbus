package ru.dz.ccu825.push;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


public class OpenHABItemsInfo {
	private Set<OpenHABItemDefinition> items = new HashSet<OpenHABItemDefinition>(); 
	
	OpenHABItemsInfo()
	{
		
	}
	
	// http://www.mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
	
	void parseItemsXML(String url) throws SAXException, IOException, ParserConfigurationException
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse( url );
		
		Element root = doc.getDocumentElement();
		root.normalize();
		 
		//System.out.println("Root element :" + root.getNodeName());
		
		Set<OpenHABItemDefinition> newItems = new HashSet<OpenHABItemDefinition>();
	 
		if (root.hasChildNodes()) 
		{
			
			NodeList nodeList = root.getChildNodes();
			for (int count = 0; count < nodeList.getLength(); count++) 
			{
				 
				Node tempNode = nodeList.item(count);
				
				//System.out.println("element :" + tempNode.getNodeName());
				
				if(!tempNode.getNodeName().equals("item"))
					continue;
				 		
				loadItem(tempNode,newItems);
				
			}
		}
		
		items = newItems; // atomic?
		
		/*
		NodeList nList = doc.getElementsByTagName("staff");
	 
		System.out.println("----------------------------");
	 
		for (int temp = 0; temp < nList.getLength(); temp++) {
	 
			Node nNode = nList.item(temp);
	 
			System.out.println("\nCurrent Element :" + nNode.getNodeName());
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 
				Element eElement = (Element) nNode;
	 
				System.out.println("Staff id : " + eElement.getAttribute("id"));
				System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
				System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
				System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
				System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());
	 
			}
		}
		*/		
	}

	private void loadItem(Node iNode, Set<OpenHABItemDefinition> newItems) 
	{
		String type = null, name = null, state = null;

		NodeList nodeList = iNode.getChildNodes();
		for (int count = 0; count < nodeList.getLength(); count++) 
		{
			Node iInfo = nodeList.item(count);
			
			//System.out.println("item element :" + iInfo.getNodeName());
			
			if(iInfo.getNodeName().equalsIgnoreCase("type"))
				type = iInfo.getTextContent();

			if(iInfo.getNodeName().equalsIgnoreCase("name"))
				name = iInfo.getTextContent();
			
			if(iInfo.getNodeName().equalsIgnoreCase("state"))
				state = iInfo.getTextContent();
			
			
		}

		OpenHABItemDefinition iDef = new OpenHABItemDefinition(name, type);
		System.out.println(iDef+" state="+state);
		
		
		//System.out.println("item name=" + name + " type="+type+" state="+state);

		newItems.add(iDef);
		
	}
	
}
