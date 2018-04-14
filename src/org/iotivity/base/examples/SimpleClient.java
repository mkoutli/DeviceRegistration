/*
 *******************************************************************
 *
 * Copyright 2015 Intel Corporation.
 *
 *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
 */
package org.iotivity.base.examples;

import org.iotivity.base.ErrorCode;
import org.iotivity.base.ModeType;
import org.iotivity.base.ObserveType;
import org.iotivity.base.OcConnectivityType;
import org.iotivity.base.OcException;
import org.iotivity.base.OcHeaderOption;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.OcRepresentation;
import org.iotivity.base.OcResource;
import org.iotivity.base.OcResourceIdentifier;
import org.iotivity.base.PlatformConfig;
import org.iotivity.base.QualityOfService;
import org.iotivity.base.ServiceType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SimpleClient
 * <p/>
 * SimpleClient is a sample client app which should be started after the
 * simpleServer is started. It finds resources advertised by the server and
 * calls different operations on it (GET, PUT, POST, DELETE and OBSERVE).
 */
public class SimpleClient implements OcPlatform.OnResourceFoundListener, OcResource.OnGetListener,
		OcResource.OnPutListener, OcResource.OnPostListener, OcResource.OnObserveListener {

	private Map<OcResourceIdentifier, OcResource> mFoundResources = new HashMap<>();
	private OcResource mFoundLightResource = null;
	// local representation of a server's light resource
	private SmartPlug mSmartPlug = new SmartPlug();
	private Device mDevice = new Device();
	// private String oid = "smartplug";
	// private String aid = "lightOnOff";
	private static String id = "";
	private static String name = "";
	private static String type = "";
	private static String address = "";
	private static String pass = "";
	private static String manufacturer = "";
	private static String model = "";
	private static boolean value = false;
	private static boolean postComplete = false;
	private static boolean putComplete = false;
	private static boolean getComplete = false;
	private static boolean discoverComplete = false;
	private static String crudVerb = "";
	private static boolean getResponse = false;

	/**
	 * A local method to configure and initialize platform, and then search for the
	 * light resources.
	 */
	private static void startSimpleClient() {

		PlatformConfig platformConfig = new PlatformConfig(ServiceType.IN_PROC, ModeType.CLIENT_SERVER, "0.0.0.0", // By
																													// setting
																													// to
																													// "0.0.0.0",
																													// it
																													// binds
																													// to
																													// all
																													// available
																													// interfaces
				0, // Uses randomly available port
				QualityOfService.HIGH);
		msg("Configuring platform.");
		OcPlatform.Configure(platformConfig);

		SimpleClient client = new SimpleClient();
		try {
			msg("Finding all resources..");
			String requestUri = OcPlatform.WELL_KNOWN_QUERY;
			
			// + "?rt=oic.r.switch.binary";
			// + "rt=oic.r.sensor.threeaxis";
			OcPlatform.findResource("", requestUri, EnumSet.of(OcConnectivityType.CT_DEFAULT), client);
			sleep(1);

			/*
			 * Find resource is done twice so that we discover the original resources a
			 * second time. These resources will have the same uniqueidentifier (yet be
			 * different objects), so that we can verify/show the duplicate-checking code in
			 * foundResource(above);
			 */
			// msg("Finding all resources of type \"oic.r.switch.binary\" for
			// the second time");
			// OcPlatform.findResource("",
			// requestUri,
			// EnumSet.of(OcConnectivityType.CT_DEFAULT),
			// client
			// );

		} catch (OcException e) {
			msgError(TAG, e.toString());
			msg("Failed to invoke find resource API");
		}

		printLine();
	}

	/**
	 * An event handler to be executed whenever a "findResource" request completes
	 * successfully
	 *
	 * @param ocResource
	 *            found resource
	 */
	@Override
	public synchronized void onResourceFound(OcResource ocResource) {
		if (null == ocResource) {
			msg("Found resource is invalid");
			return;
		}

		if (mFoundResources.containsKey(ocResource.getUniqueIdentifier())) {
			msg("Found a previously seen resource again!");
		} else {
			msg("Found resource for the first time on server with ID: " + ocResource.getServerId());
			mFoundResources.put(ocResource.getUniqueIdentifier(), ocResource);
		}

		if (null != mFoundLightResource) {
			msg("Found another resource, ignoring");
			// Get the resource URI
			String resourceUri = ocResource.getUri();
			msg("\tURI of the resource: " + resourceUri);
			return;
		}
		// Get the resource URI
        String resourceUri = ocResource.getUri();
        // Get the resource host address
        String hostAddress = ocResource.getHost();
        msg("\tURI of the resource: " + resourceUri);
        msg("\tHost address of the resource: " + hostAddress);
        // Get the resource types
        msg("\tList of resource types: ");
        for (String resourceType : ocResource.getResourceTypes()) {
            msg("\t\t" + resourceType);
        }
        msg("\tList of resource interfaces:");
        for (String resourceInterface : ocResource.getResourceInterfaces()) {
            msg("\t\t" + resourceInterface);
        }
        // Get Resource current host
        msg("\tHost of resource: ");
        msg("\t\t" +  hostAddress);
        // Get Resource Endpoint Infomation
        msg("\tList of resource endpoints: ");
        for(String resourceEndpoint : ocResource.getAllHosts())
        {
            msg("\t\t" + resourceEndpoint);
        }

        OcConnectivityType TRANSPORT_TYPE_TO_USE = OcConnectivityType.CT_ADAPTER_IP;

        // If resource is found from ip based adapter.
        if (hostAddress.contains("coap://") ||
            hostAddress.contains("coaps://") ||
            hostAddress.contains("coap+tcp://") ||
            hostAddress.contains("coaps+tcp://"))
        {
            for(String resourceEndpoint : ocResource.getAllHosts())
            {
                if (!resourceEndpoint.equals(hostAddress) &&
                    !resourceEndpoint.contains("coap+rfcomm"))
                {
                    String newHost = resourceEndpoint;
                    if (newHost.contains("tcp"))
                    {
                        TRANSPORT_TYPE_TO_USE = OcConnectivityType.CT_ADAPTER_TCP;
                    }
                    else
                    {
                        TRANSPORT_TYPE_TO_USE = OcConnectivityType.CT_ADAPTER_IP;
                    }
                    // Change Resource host if another host exists
                    msg("\tChange host of resource endpoints");
                    msg("\t\t" + "Current host is " + ocResource.setHost(newHost));
                    break;
                }
            }
        }
        msg("\tList of resource connectivity types:");
        for (OcConnectivityType connectivityType : ocResource.getConnectivityTypeSet()) {
            msg("\t\t" + connectivityType);
        }
		printLine();

		if (!crudVerb.equalsIgnoreCase("discover")) {

			// In this example we are only interested in the smartplug resources
			if (resourceUri.equals("/a/devices")) {
				// Assign resource reference to a global variable to keep it
				// from being
				// destroyed by the GC when it is out of scope.
				mFoundLightResource = ocResource;

				// Call a local method which will internally invoke "get" API on
				// the foundLightResource
				if (crudVerb.equalsIgnoreCase("get")) {
					getDeviceResourceRepresentation();
				}
				if (crudVerb.equalsIgnoreCase("post")) {
					postDeviceRepresentation();
				}
				if (crudVerb.equalsIgnoreCase("put")) {
					putDeviceRepresentation();
				}
				if (crudVerb.equalsIgnoreCase("delete")) {
					deleteDeviceRepresentation();
				}
			}
		}
	}

	@Override
	public synchronized void onFindResourceFailed(Throwable throwable, String uri) {
		msg("findResource request has failed");
		msgError(TAG, throwable.toString());
		System.out.println(uri);
	}

	/**
	 * Local method to get representation of a found light resource
	 */
	private void getDeviceResourceRepresentation() {
		msg("Getting Light Representation...");

		Map<String, String> queryParams = new HashMap<>();
		try {
			// Invoke resource's "get" API with a OcResource.OnGetListener event
			// listener implementation
			sleep(1);
			mFoundLightResource.get(queryParams, this);
		} catch (OcException e) {
			msgError(TAG, e.toString());
			msg("Error occurred while invoking \"get\" API");
		}
	}

	/**
	 * An event handler to be executed whenever a "get" request completes
	 * successfully
	 *
	 * @param list
	 *            list of the header options
	 * @param ocRepresentation
	 *            representation of a resource
	 */
	@Override
	public synchronized void onGetCompleted(List<OcHeaderOption> list, OcRepresentation ocRepresentation) {
		msg("GET request was successful");
		msg("Resource URI: " + ocRepresentation.getUri());

		try {
			// Read attribute values into local representation of a light
			mSmartPlug.setOcRepresentation(ocRepresentation);
		} catch (OcException e) {
			msgError(TAG, e.toString());
			msg("Failed to read the attributes of a light resource");
		}
		msg("SmartPlug attributes: ");
		msg(mSmartPlug.toString());
		getResponse = mSmartPlug.getValue();
		printLine();
		getComplete = true;
		// Call a local method which will internally invoke put API on the
		// foundLightResource
		// putLightRepresentation();
		// postLightRepresentation();
	}

	/**
	 * An event handler to be executed whenever a "get" request fails
	 *
	 * @param throwable
	 *            exception
	 */
	@Override
	public synchronized void onGetFailed(Throwable throwable) {
		if (throwable instanceof OcException) {
			OcException ocEx = (OcException) throwable;
			msgError(TAG, ocEx.toString());
			ErrorCode errCode = ocEx.getErrorCode();
			// do something based on errorCode
			msg("Error code: " + errCode);
		}
		msg("Failed to get representation of a found light resource");
	}

	/**
	 * Local method to put a different state for this light resource
	 */
	private void putDeviceRepresentation() {
		// set new values
		mDevice.setId(id);
		mDevice.setName(name);
		mDevice.setType(type);
		mDevice.setAddress(address);
		mDevice.setPassword(pass);
		mDevice.setManufacturer(manufacturer);
		mDevice.setModel(model);

		msg("Putting device representation...");
		OcRepresentation representation = null;
		try {
			representation = mDevice.getOcRepresentation();
		} catch (OcException e) {
			msgError(TAG, e.toString());
			msg("Failed to get OcRepresentation from device");
		}

		Map<String, String> queryParams = new HashMap<>();

		try {
			sleep(1);
			// Invoke resource's "put" API with a new representation, query
			// parameters and
			// OcResource.OnPutListener event listener implementation
			mFoundLightResource.put(representation, queryParams, this);
		} catch (OcException e) {
			msgError(TAG, e.toString());
			msg("Error occurred while invoking \"put\" API");
		}
	}

	/**
	 * An event handler to be executed whenever a "put" request completes
	 * successfully
	 *
	 * @param list
	 *            list of the header options
	 * @param ocRepresentation
	 *            representation of a resource
	 */
	@Override
	public synchronized void onPutCompleted(List<OcHeaderOption> list, OcRepresentation ocRepresentation) {
		msg("PUT request was successful");
		// try {
		// mSmartPlug.setOcRepresentation(ocRepresentation);
		// } catch (OcException e) {
		// msgError(TAG, e.toString());
		// msg("Failed to create Light representation");
		// }
		// msg("SmartPlug attributes: ");
		// msg(mSmartPlug.toString());
		// printLine();

		putComplete = true;
		// Call a local method which will internally invoke post API on the
		// foundLightResource
		// postLightRepresentation();
	}

	/**
	 * An event handler to be executed whenever a "put" request fails
	 *
	 * @param throwable
	 *            exception
	 */
	@Override
	public synchronized void onPutFailed(Throwable throwable) {
		if (throwable instanceof OcException) {
			OcException ocEx = (OcException) throwable;
			msgError(TAG, ocEx.toString());
			ErrorCode errCode = ocEx.getErrorCode();
			// do something based on errorCode
			msg("Error code: " + errCode);
		}
		msg("Failed to \"put\" a new representation");
	}

	/**
	 * Local method to post a different state for this light resource
	 */
	private void postDeviceRepresentation() {
		// set new values
		// mSmartPlug.setValue(value);
		// mLight.setPower(105);
		mDevice.setId(id);
		mDevice.setName(name);
		mDevice.setType(type);
		mDevice.setAddress(address);
		mDevice.setPassword(pass);
		mDevice.setManufacturer(manufacturer);
		mDevice.setModel(model);

		msg("Posting Registration representation...");
		OcRepresentation representation = null;

		try {
			representation = mDevice.getOcRepresentation();
			// rep.setValue("user", "1");
			// rep.setValue("name", name);
			// rep.setValue("type", type);
			// rep.setValue("mac", mac);
			// rep.setValue("pass", pass);
		} catch (OcException e) {
			msgError(TAG, e.toString());
			msg("Failed to set OcRepresentation");
		}

		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("operation", "register");
		// if (value) {
		// queryParams.put(aid, "on");
		// } else {
		// queryParams.put(aid, "off");
		// }

		try {
			// sleep(1);
			// Invoke resource's "post" API with a new representation, query
			// parameters and
			// OcResource.OnPostListener event listener implementation
			mFoundLightResource.post(representation, queryParams, this);
		} catch (OcException e) {
			msgError(TAG, e.toString());
			msg("Error occurred while invoking \"post\" API");
		}
	}

	/**
	 * An event handler to be executed whenever a "post" request completes
	 * successfully
	 *
	 * @param list
	 *            list of the header options
	 * @param ocRepresentation
	 *            representation of a resource
	 */
	@Override
	public synchronized void onPostCompleted(List<OcHeaderOption> list, OcRepresentation ocRepresentation) {
		msg("POST request was successful");
		try {
			if (ocRepresentation.hasAttribute(OcResource.CREATED_URI_KEY)) {
				msg("\tUri of the created resource: " + ocRepresentation.getValue(OcResource.CREATED_URI_KEY));
			} else {
				if (ocRepresentation.hasAttribute("name") && ocRepresentation.hasAttribute("mac")
						&& ocRepresentation.hasAttribute("password") && ocRepresentation.hasAttribute("type")) {
					mDevice.setOcRepresentation(ocRepresentation);
					msg(mDevice.toString());
				}
			}
		} catch (OcException e) {
			msgError(TAG, e.toString());
		}

		postComplete = true;

	}

	/**
	 * An event handler to be executed whenever a "post" request fails
	 *
	 * @param throwable
	 *            exception
	 */
	@Override
	public synchronized void onPostFailed(Throwable throwable) {
		if (throwable instanceof OcException) {
			OcException ocEx = (OcException) throwable;
			msgError(TAG, ocEx.toString());
			ErrorCode errCode = ocEx.getErrorCode();
			// do something based on errorCode
			msg("Error code: " + errCode);
		}
		msg("Failed to \"post\" a new representation");
	}

	/**
	 * Declare and implement a second OcResource.OnPostListener
	 */
	// OcResource.OnPostListener onPostListener2 = new
	// OcResource.OnPostListener() {
	// /**
	// * An event handler to be executed whenever a "post" request completes
	// successfully
	// * @param list list of the header options
	// * @param ocRepresentation representation of a resource
	// */
	// @Override
	// public synchronized void onPostCompleted(List<OcHeaderOption> list,
	// OcRepresentation ocRepresentation) {
	// msg("Second POST request was successful");
	// try {
	// if (ocRepresentation.hasAttribute(OcResource.CREATED_URI_KEY)) {
	// msg("\tUri of the created resource: " +
	// ocRepresentation.getValue(OcResource.CREATED_URI_KEY));
	// } else {
	// mLight.setOcRepresentation(ocRepresentation);
	// msg(mLight.toString());
	// }
	// } catch (OcException e) {
	// msgError(TAG, e.toString());
	// }
	//
	// //Call a local method which will internally invoke observe API on the
	// foundLightResource
	// observeFoundLightResource();
	// }
	//
	// /**
	// * An event handler to be executed whenever a "post" request fails
	// *
	// * @param throwable exception
	// */
	// @Override
	// public synchronized void onPostFailed(Throwable throwable) {
	// if (throwable instanceof OcException) {
	// OcException ocEx = (OcException) throwable;
	// msgError(TAG, ocEx.toString());
	// ErrorCode errCode = ocEx.getErrorCode();
	// //do something based on errorCode
	// msg("Error code: " + errCode);
	// }
	// msg("Failed to \"post\" a new representation");
	// }
	// };
	
	private void deleteDeviceRepresentation() {
		mDevice.setId(id);
		msg("Deleting device representation...");
		OcRepresentation representation = null;
		try {
			representation = mDevice.getOcRepresentation();
		} catch (OcException e) {
			msgError(TAG, e.toString());
			msg("Failed to set OcRepresentation");
		}

		Map<String, String> queryParams = new HashMap<>();
		queryParams.put("operation", "delete");
		

		try {
			mFoundLightResource.post(representation, queryParams, this);
		} catch (OcException e) {
			msgError(TAG, e.toString());
			msg("Error occurred while invoking \"post\" API");
		}
	}

	/**
	 * Local method to start observing this light resource
	 */
	private void observeFoundLightResource() {
		try {
			sleep(1);
			// Invoke resource's "observe" API with a observe type, query
			// parameters and
			// OcResource.OnObserveListener event listener implementation
			mFoundLightResource.observe(ObserveType.OBSERVE, new HashMap<String, String>(), this);
		} catch (OcException e) {
			msgError(TAG, e.toString());
			msg("Error occurred while invoking \"observe\" API");
		}
	}

	// holds current number of observations
	private static int mObserveCount = 0;

	/**
	 * An event handler to be executed whenever a "post" request completes
	 * successfully
	 *
	 * @param list
	 *            list of the header options
	 * @param ocRepresentation
	 *            representation of a resource
	 * @param sequenceNumber
	 *            sequence number
	 */
	@Override
	public synchronized void onObserveCompleted(List<OcHeaderOption> list, OcRepresentation ocRepresentation,
			int sequenceNumber) {
		if (OcResource.OnObserveListener.REGISTER == sequenceNumber) {
			msg("Observe registration action is successful:");
		} else if (OcResource.OnObserveListener.DEREGISTER == sequenceNumber) {
			msg("Observe De-registration action is successful");
		} else if (OcResource.OnObserveListener.NO_OPTION == sequenceNumber) {
			msg("Observe registration or de-registration action is failed");
		}

		msg("OBSERVE Result:");
		msg("\tSequenceNumber:" + sequenceNumber);
		try {
			mSmartPlug.setOcRepresentation(ocRepresentation);
		} catch (OcException e) {
			msgError(TAG, e.toString());
			msg("Failed to get the attribute values");
		}
		msg(mSmartPlug.toString());

		if ((++mObserveCount) == 11) {
			msg("Cancelling Observe...");
			try {
				mFoundLightResource.cancelObserve();
			} catch (OcException e) {
				msgError(TAG, e.toString());
				msg("Error occurred while invoking \"cancelObserve\" API");
			}
			msg("DONE");

			// prepare for the next restart of the SimpleClient
			resetGlobals();
		}
	}

	/**
	 * An event handler to be executed whenever a "observe" request fails
	 *
	 * @param throwable
	 *            exception
	 */
	@Override
	public synchronized void onObserveFailed(Throwable throwable) {
		if (throwable instanceof OcException) {
			OcException ocEx = (OcException) throwable;
			msgError(TAG, ocEx.toString());
			ErrorCode errCode = ocEx.getErrorCode();
			// do something based on errorCode
			msg("Error code: " + errCode);
		}
		msg("Observation of the found light resource has failed");
	}

	// ******************************************************************************
	// End of the OIC specific code
	// ******************************************************************************

	private final static String TAG = SimpleClient.class.getSimpleName();

	public static void callPost(String i, String n, String t, String a, String p, String man, String mod) {
		if (i != null) {
			id = i;
		} else {
			System.out.println("Id is " + i + ", can't register device");
			return;
		}
		if (n != null) {
			name = n;
		} else {
			name = "";
		}
		if (t != null) {
			type = t;
		} else {
			type = "";
		}
		if (a != null) {
			address = a;
		} else {
			address = "";
		}
		if (p != null) {
			pass = p;
		} else {
			pass = "";
		}
		if (man != null) {
			manufacturer = man;
		} else {
			manufacturer = "";
		}
		if (mod != null) {
			model = mod;
		} else {
			model = "";
		}

		crudVerb = "post";
		boolean running = true;
		startSimpleClient();
		while (running) {
			if (getPostCompleted()) {
				break;
			}
			sleep(1);
		}

	}

	public static void callPut(String i, String n, String t, String a, String p, String man, String mod) {
		if (i != null) {
			id = i;
		} else {
			System.out.println("Id is " + i + ", can't update device");
			return;
		}
		if (n != null) {
			name = n;
		} else {
			name = "";
		}
		if (t != null) {
			type = t;
		} else {
			type = "";
		}
		if (a != null) {
			address = a;
		} else {
			address = "";
		}
		if (p != null) {
			pass = p;
		} else {
			pass = "";
		}
		if (man != null) {
			manufacturer = man;
		} else {
			manufacturer = "";
		}
		if (mod != null) {
			model = mod;
		} else {
			model = "";
		}

		crudVerb = "put";
		boolean running = true;
		startSimpleClient();
		while (running) {
			if (getPutCompleted()) {
				break;
			}
			sleep(1);
		}
	}
	
	public static void callDelete(String i) {
		if (i != null) {
			id = i;
		} else {
			System.out.println("Id is " + i + ", can't delete device");
			return;
		}
		crudVerb = "delete";
		boolean running = true;
		startSimpleClient();
		while (running) {
			if (getPostCompleted()) {
				break;
			}
			sleep(1);
		}
	}

	// public static boolean callGet(String actionid, String objid) {
	// aid = actionid;
	// oid = objid;
	// crudVerb = "get";
	// boolean running = true;
	// startSimpleClient();
	// while (running) {
	// if (getGetCompleted()) {
	// return getResponse;
	// }
	// sleep(1);
	// }
	// return getResponse;
	// }

	// public static boolean callDiscover() {
	//
	// crudVerb = "discover";
	// boolean running = true;
	// startSimpleClient();
	// while (running) {
	// if (getDiscoverCompleted()) {
	// return mFoundResources;
	// }
	// sleep(1);
	// }
	// return mFoundResources;
	// }

	private static void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			msgError(TAG, e.toString());
		}
	}

	private static void msg(final String text) {
		System.out.println("[O]" + TAG + " | " + text);
	}

	private static void msg(final String tag, final String text) {
		System.out.println("[O]" + tag + " | " + text);
	}

	private static void msgError(final String tag, final String text) {
		System.out.println("[E]" + tag + " | " + text);
	}

	private static void printLine() {
		msg("------------------------------------------------------------------------");
	}

	private synchronized void resetGlobals() {
		mFoundLightResource = null;
		mFoundResources.clear();
		mSmartPlug = new SmartPlug();
		mObserveCount = 0;
	}

	private static boolean getPostCompleted() {
		return postComplete;
	}

	private static boolean getPutCompleted() {
		return putComplete;
	}

	private static boolean getGetCompleted() {
		return getComplete;
	}

	private static boolean getDiscoverCompleted() {
		return discoverComplete;
	}

}
