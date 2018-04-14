package org.iotivity.base.examples;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;

public class Device {

	public static final String ID_KEY =  "id";
	public static final String NAME_KEY =  "name";
    public static final String TYPE_KEY =  "type";
    public static final String ADDRESS_KEY =  "address";
    public static final String PASSWORD_KEY =  "password";
    public static final String MANUFACTURER_KEY =  "manufacturer";
    public static final String MODEL_KEY =  "model";


    private String mId;
    private String mName;
    private String mType;
    private String mAddress;
    private String mPass;
    private String mManufacturer;
    private String mModel;


    public Device() {
        mId = "";
    	mName = "";
        mType = "";
        mAddress = "";
        mPass= "";
        mManufacturer = "";
        mModel= "";

    }

    public void setOcRepresentation(OcRepresentation rep) throws OcException {
        mId = rep.getValue(ID_KEY);
    	mName = rep.getValue(NAME_KEY);
        mType = rep.getValue(TYPE_KEY);
        mAddress = rep.getValue(ADDRESS_KEY);
        mPass = rep.getValue(PASSWORD_KEY);
        mManufacturer = rep.getValue(MANUFACTURER_KEY);
        mModel= rep.getValue(MODEL_KEY);
    }

    public OcRepresentation getOcRepresentation() throws OcException {
        OcRepresentation rep = new OcRepresentation();
        rep.setValue(ID_KEY, mId);
        rep.setValue(NAME_KEY, mName);
        rep.setValue(TYPE_KEY, mType);
        rep.setValue(ADDRESS_KEY, mAddress);
        rep.setValue(PASSWORD_KEY, mPass);
        rep.setValue(MANUFACTURER_KEY, mManufacturer);
        rep.setValue(MODEL_KEY, mModel);
        return rep;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }
    
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }
    
    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String getPassword() {
        return mPass;
    }

    public void setPassword(String pass) {
        this.mPass = pass;
    }
    
    public String getManufacturer() {
        return mManufacturer;
    }

    public void setManufacturer(String man) {
        this.mManufacturer = man;
    }
    
    public String getModel() {
        return mModel;
    }

    public void setModel(String mod) {
        this.mModel = mod;
    }

    @Override
    public String toString() {
        return "\t" + NAME_KEY + ": " + mName +
        		"\n\t" + TYPE_KEY + ": " + mType;

    }
}
