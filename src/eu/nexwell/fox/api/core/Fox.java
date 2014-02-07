package eu.nexwell.fox.api.core;

import java.util.ArrayList;
import java.util.TreeMap;

import eu.nexwell.fox.api.devices.FoxDeviceDimm;
import eu.nexwell.fox.api.devices.FoxDeviceLed;
import eu.nexwell.fox.api.devices.FoxDeviceNet;
import eu.nexwell.fox.api.devices.FoxDeviceOut;
import eu.nexwell.fox.api.devices.FoxDeviceTouch;

public class Fox {

	public final static int maxDevicesCount = 32;
	public final static int minDeviceAddress = 0;
	public final static int maxDeviceAddress = maxDevicesCount - 1;
	
	private TreeMap<Integer, FoxDevice> devices;
	private FoxMessenger messenger = null;
	
	public Fox() {
		devices = new TreeMap<Integer, FoxDevice>();
	}
	
	public void setMessenger(FoxMessenger messenger) {
		this.messenger = messenger;
	}
	
	public void addDevice(FoxDevice dev) throws FoxException {
		if (dev == null)
			throw new FoxException("Null device");
		if (devices.size() >= maxDevicesCount)
			throw new FoxException(String.format("Cannot add more than %d devices", maxDevicesCount));
		if (getDevice(dev.getAddress()) != null)
			throw new FoxException(String.format("Device with address %d already added", dev.getAddress()));
		dev.setParentSystem(this);
		devices.put(dev.getAddress(), dev);
	}
	
	public void addDevices(FoxDevice[] devs) throws FoxException {
		for (FoxDevice dev : devs) {
			addDevice(dev);
		}
	}
	
	public FoxDevice getDevice(int address) {
		return devices.get(address);
	}
	
	public FoxDevice[] getDevices() {
		ArrayList<FoxDevice> devices = new ArrayList<FoxDevice>();
		for (FoxDevice dev : this.devices.values())
			devices.add(dev);
		return (FoxDevice[]) devices.toArray();
	}
	
	public FoxDevice[] searchDevices() throws FoxException {
		ArrayList<FoxDevice> devices = new ArrayList<FoxDevice>();
		for (int i = minDeviceAddress; i <= maxDeviceAddress; i++) {
			FoxDevice device = new FoxDevice(i);
			
			FoxMessageHello msgTx = new FoxMessageHello();
			msgTx.setDevice(device);
			write(msgTx);
			
			FoxMessageMe msgRx = new FoxMessageMe();
			read(msgRx);
			String type = msgRx.getType();
			
			device = null;
			if (type.equals("nxw.fox.out"))
				device = new FoxDeviceOut(i);
			else
			if (type.equals("nxw.fox.dimm"))
				device = new FoxDeviceDimm(i);
			else
			if (type.equals("nxw.fox.led"))
				device = new FoxDeviceLed(i);
			else
			if (type.equals("nxw.fox.net"))
				device = new FoxDeviceNet(i);
			else
			if (type.equals("nxw.fox.touch"))
				device = new FoxDeviceTouch(i);
			
			if (device != null)
				devices.add(device);
		}
		return (FoxDevice[]) devices.toArray();
	}
	
	public void reboot() throws FoxException {
		FoxMessageBoot msg = new FoxMessageBoot();
		msg.setDeviceAll();
		write(msg);
	}
	
	void write(FoxMessage msg) throws FoxException {
		if (msg == null)
			throw new FoxException("Null message");
		if (messenger == null)
			throw new FoxException("Null messenger");
		messenger.write(msg.prepare().trim() + "\r\n");
	}
	
	void read(FoxMessage msg) throws FoxException {
		if (messenger == null)
			throw new FoxException("Null messenger");
		msg.interpret(messenger.read().trim());
	}
}