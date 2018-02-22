package reg.devices;

import org.iotivity.base.examples.SimpleClient;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WorkflowClass {

	//static String DIR = System.getProperty("java.library.path");
	
	public static class RegInfo {
		
		@SerializedName("name")
		@Expose
		private String name;
		
		@SerializedName("type")
		@Expose
		private String type;
		
		@SerializedName("address")
		@Expose
		private String address;
		
		@SerializedName("password")
		@Expose
		private String password;
		
		@SerializedName("manufacturer")
		@Expose
		private String manufacturer;
		
		@SerializedName("model")
		@Expose
		private String model;
		
		RegInfo(){
			
		}
		
		RegInfo(String name, String type, String address, String password, String manufacturer, String model){
			this.name = name;
			this.type = type;
			this.address = address;
			this.password = password;
			this.manufacturer = manufacturer;
			this.model = model;
		}

		public String getName() {
		return name;
		}

		public void setName(String name) {
		this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
		public String getAddress() {
		return address;
		}

		public void setAddress(String address) {
		this.address = address;
		}

		public String getPassword() {
		return password;
		}

		public void setPassword(String pass) {
		this.password = pass;
		}


		public String getManufacturer() {
			return manufacturer;
		}

		public void setManufacturer(String manufacturer) {
			this.manufacturer = manufacturer;
		}

		public String getModel() {
			return model;
		}

		public void setModel(String model) {
			this.model = model;
		}
		
		
	}
	
	public static class Response {
		@SerializedName("message")
		@Expose
		private String message;

		public Response() {
		}

		public Response(String message) {
			this.message = message;
		}

		public void setmessage(String message) {
			this.message = message;
		}

		public String getmessage() {
			return message;
		}
	}
	
	
	public Response parseResponse(RegInfo info){
		//System.out.println(DIR);
		Integer id = SQLiteJDBC.insert(info.getName(), info.getType(), info.getAddress(), info.getPassword(), info.getManufacturer(), info.getModel());
		SimpleClient.callPost(id, info.getName(), info.getType(), info.getAddress(), info.getPassword(), info.getManufacturer(), info.getModel());
		return new Response("The device with name: " + info.getName() + " was registered.");
	}
}
