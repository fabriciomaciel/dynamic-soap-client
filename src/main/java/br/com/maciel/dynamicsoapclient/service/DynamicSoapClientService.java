package br.com.maciel.dynamicsoapclient.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.*;
import javax.xml.ws.Dispatch;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;


@Service
public class DynamicSoapClientService {


    private final String NAMESPACE = "http://tempuri.org/";
    private final String SERVICE_NAME = "Calculator";
    private final String PORT_NAME = "CalculatorSoap12";
    private final String OPERATION_NAME = "Add";
    private final String OPERATION_PREFIX = "tem";

    public void obterDadosWebService(String wsdlPath) {
        URL wsdlURL;
        try {
            wsdlURL = new URL(wsdlPath);
            QName serviceName = new QName(NAMESPACE, SERVICE_NAME);
            QName portName = new QName(NAMESPACE,PORT_NAME);
            javax.xml.ws.Service service = javax.xml.ws.Service.create(wsdlURL, serviceName);
            Dispatch<SOAPMessage> disp = service.createDispatch(portName, SOAPMessage.class, javax.xml.ws.Service.Mode.MESSAGE);
            MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
            SOAPMessage request = mf.createMessage();
            SOAPPart part = request.getSOAPPart();
            SOAPEnvelope env = part.getEnvelope();
            SOAPBody body = env.getBody();
            SOAPElement operation = body.addChildElement(OPERATION_NAME, OPERATION_PREFIX,  NAMESPACE);
            // Popula os dados a serem enviados no request

            operation.addChildElement("intA", OPERATION_PREFIX).addTextNode("10");
            operation.addChildElement("intB", OPERATION_PREFIX).addTextNode("2");
            request.saveChanges();

            //Log do request
            ByteArrayOutputStream byteRequest = new ByteArrayOutputStream();
            request.writeTo(byteRequest);
            System.out.println("Request a ser enviado:\r\n" + byteRequest.toString(StandardCharsets.UTF_8.name()));

            // Authenticação
//            disp.getRequestContext().put(Dispatch.USERNAME_PROPERTY, username);
//            disp.getRequestContext().put(Dispatch.PASSWORD_PROPERTY, password);
            SOAPMessage response = disp.invoke(request);
            // Debug do response
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            response.writeTo(byteArray);
            System.out.println("Resposta recebida:\r\n" + byteArray.toString(StandardCharsets.UTF_8.name()));

            NodeList nodes;
            // Inicial
            nodes = response.getSOAPBody().getElementsByTagName("AddResult");
            System.out.println("Valor recebido: " + nodes.item(0).getTextContent());

        } catch (Exception e) {
            System.out.println("ERRO:" +  e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     *
     */
    private static final String PORT = "port";
    private static final String PORT_1 = "wsdl:port";
    private static final String SERVICE = "service";
    private static final String SERVICE_1 = "wsdl:service";
    private static final String COMMENT = "#comment";
    private static final String TARGET_NAMESPACE = "targetNamespace";
    private static final String OPERATION = "operation";
    private static final String OPERATION_1 = "wsdl:operation";

    public void parseWsdlFile(String wsdlPath) throws FileNotFoundException, SAXException, IOException, ParserConfigurationException {
        String targetUrl = wsdlPath;
        if(!wsdlPath.contains("?wsdl"))
            targetUrl += "?wsdl";
        //Read WSDl File from URL
        URL url = new URL(targetUrl);
        URLConnection uc = url.openConnection();
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(uc.getInputStream());

        //Remove Comments from Parse
        for (int index = 0; index < document.getChildNodes().getLength(); index++) {
            if (document.getFirstChild().getNodeName().equalsIgnoreCase(COMMENT)) {
                document.removeChild(document.getFirstChild());
            }
        }
        //Name space
        System.out.println("TARGET_NAMESPACE = " + document.getFirstChild().getAttributes().getNamedItem(TARGET_NAMESPACE).getNodeValue());


        //List all Operations
        NodeList nodeListOfOperations = null;
        if ((document.getElementsByTagName(OPERATION).getLength() > 0)
                || (document.getElementsByTagName(OPERATION_1).getLength() > 0)) {
            if (document.getElementsByTagName(OPERATION).getLength() > 0) {
                nodeListOfOperations = document.getElementsByTagName(OPERATION);
            } else if (document.getElementsByTagName(OPERATION_1).getLength() > 0) {
                nodeListOfOperations = document.getElementsByTagName(OPERATION_1);
            }
        }
        for (int i = 0; i < nodeListOfOperations.getLength(); i++) {
            System.out.println("OPERATION-name = " +
                    nodeListOfOperations.item(i).getAttributes().getNamedItem("name").getNodeValue()
                     + " OPERATION-parameterOrder = " +
                    nodeListOfOperations.item(i).getAttributes().getNamedItem("parameterOrder"));
        }


        //List all Services
        if (document.getElementsByTagName(SERVICE).getLength() > 0) {
            NodeList nodeListOfService = document.getElementsByTagName(SERVICE);
            for (int i = 0; i < nodeListOfService.getLength(); i++) {
                System.out.println("SERVICE = " + nodeListOfService.item(i).getAttributes().getNamedItem("name"));
            }
        }
        if (document.getElementsByTagName(SERVICE_1).getLength() > 0) {
            NodeList nodeListOfService = document.getElementsByTagName(SERVICE_1);
            for (int i = 0; i < nodeListOfService.getLength(); i++) {
                System.out.println("SERVICE = " + nodeListOfService.item(i).getAttributes().getNamedItem("name"));
            }
        }

        //List all Ports
        if (document.getElementsByTagName(PORT).getLength() > 0) {
            NodeList nodeListOfPorts = document.getElementsByTagName(PORT);
            for (int i = 0; i < nodeListOfPorts.getLength(); i++) {
                System.out.println("PORT = " + nodeListOfPorts.item(i).getAttributes().getNamedItem("name"));
            }
        }
        if (document.getElementsByTagName(PORT_1).getLength() > 0) {
            NodeList nodeListOfPorts = document.getElementsByTagName(PORT_1);
            for (int i = 0; i < nodeListOfPorts.getLength(); i++) {
                System.out.println("PORT = " + nodeListOfPorts.item(i).getAttributes().getNamedItem("name"));
            }
        }
    }

}
