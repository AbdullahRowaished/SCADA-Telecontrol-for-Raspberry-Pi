package iec_60870_5_104.apdu;

import java.util.Set;

import converters.Converter;
import converters.Octet;
import exceptions.ElementTypeException;
import exceptions.InformationObjectAddressException;
import exceptions.NotBinaryException;
import exceptions.StdException;
import iec_60870_5_104.apdu.asdu.dui.Variable_Structure_Qualifier;

import java.util.Collections;
import java.util.HashSet;

/**
 * ASDU, or Application Service Data Unit - is the sub-unit of data containing
 * the actual information to be conveyed between systems. It contains: a Data
 * Unit Identifier; an array of Information Object.
 * 
 * @author ar421
 *
 */
public class ASDU {
	@Override
	public String toString() {
		String string = "ASDU{";
		string += "DATAUNIT{";
		string += getData_unit_identifier().toString() + "},";
		string += "INFORMATIONOBJECTS{";
		for(Information_Object object : information_objects) {
			string += object.toString() + ",";
		}
		string += "END}}";
		return string;
	}
	private Data_Unit_Identifier data_unit_identifier;
	Set<Information_Object> information_objects;
	private Octet[] data;
	int j, t;
	public static int dc; //data and information counters
	static int ic;

	/**
	 * creates a new ASDU instance
	 * 
	 * @param duid
	 * @param ios
	 * @throws ElementTypeException
	 * @throws InformationObjectAddressException
	 * @throws NotBinaryException 
	 */
	public ASDU(Octet[] data, int j, int t)
			throws StdException {
		dc = 0;
		ic = 0;
		this.setData(data);
		this.j = j;
		this.t = t;
		setData_unit_identifier(new Data_Unit_Identifier(this, 2, 2));
		information_objects = Collections.synchronizedSet(new HashSet<>());
		fillObjects();
	}
	/**
	 * A much simpler constructor used as API for programmers
	 * @param type
	 * @param values
	 * @param addressing
	 * @throws StdException 
	 * @throws NullPointerException 
	 */
	public ASDU(int[] dut, int[] values) throws NullPointerException, StdException {
		ic = 0;
		dc = 0;
		j = 2;
		t = 3;
		data = new Octet[values.length + 7];
		int counter = 0;
		for (int i = 0; i < 4; i++) {
			if (i == 0 || i == 1) {
				data[counter++] = Converter.parse(Converter.lex(Octet.getBits(dut[i], 8).toCharArray()))[0];
			} else {
				Octet[] ds = Converter.parse(Converter.lex(Octet.getBits(dut[i], 16).toCharArray()));
				data[counter++] = ds[0];
				data[counter++] = ds[1];
			}
		}
		for (int i = 0; i < values.length; i++) {
			Octet[] ds = Converter.parse(Converter.lex(Octet.getBits(values[i], 8).toCharArray()));
			data[counter++] = ds[0];
		}
		setData_unit_identifier(new Data_Unit_Identifier(this, 2, 2));
		information_objects = Collections.synchronizedSet(new HashSet<>());
		fillObjects();
	}
	/**
	 * constructor specifically made for the demonstration
	 * @param type
	 * @param val1
	 * @param val2
	 */
	public ASDU(String type, int val1, int val2) {
		if (type.equals("LED")) {
			this.setData_unit_identifier(new Data_Unit_Identifier(49, 1, 6, val1));
			this.information_objects = Collections.synchronizedSet(new HashSet<>());
			Information_Object object = new Information_Object(0);
			object.information_elements = Collections.synchronizedSet(new HashSet<>());
			information_objects.add(object);
		}
	}
	
	private void fillObjects() throws StdException {
		Variable_Structure_Qualifier vsq = getData_unit_identifier().getDut().getVsq();
		if (!vsq.isAddressing_mode()) {
			// many objects single elements
			while (dc < getData().length && ic <= vsq.getNumber_of_addressables()) {
				information_objects.add(new Information_Object(this));
				ic++;
			}
		} else if (vsq.isAddressing_mode()) {
			// one object many elements
			information_objects.add(new Information_Object(this));
		} else {
			throw new NotBinaryException();
		}
	}
	public Octet[] getData() {
		return data;
	}
	public void setData(Octet[] data) {
		this.data = data;
	}
	public Data_Unit_Identifier getData_unit_identifier() {
		return data_unit_identifier;
	}
	public void setData_unit_identifier(Data_Unit_Identifier data_unit_identifier) {
		this.data_unit_identifier = data_unit_identifier;
	}
}