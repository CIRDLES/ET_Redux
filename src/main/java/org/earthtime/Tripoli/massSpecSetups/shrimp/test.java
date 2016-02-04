/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.earthtime.Tripoli.massSpecSetups.shrimp;

import java.io.File;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.cirdles.shrimp.PrawnFile;
import org.cirdles.shrimp.PrawnFile.Run.Set.Scan.Measurement;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File file = new File("/Users/sbowring/Documents/Development_XSD/100142_G6147_10111109.43 10.33.37 AM.xml");

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            PrawnFile pf = (PrawnFile) jaxbUnmarshaller.unmarshal(file);
            List<Measurement> measurements = pf.getRun().get(0).getSet().getScan().get(0).getMeasurement();
            for (int i = 0; i < measurements.size(); i++) {
                System.out.println(pf.getRun().get(0).getSet().getScan().get(0).getMeasurement().get(i).getData().get(0).getName());
                System.out.println(pf.getRun().get(0).getSet().getScan().get(0).getMeasurement().get(i).getData().get(0).getValue());
                System.out.println(pf.getRun().get(0).getSet().getScan().get(0).getMeasurement().get(i).getData().get(1).getValue());
            }

        } catch (JAXBException jAXBException) {
        }
    }

}
