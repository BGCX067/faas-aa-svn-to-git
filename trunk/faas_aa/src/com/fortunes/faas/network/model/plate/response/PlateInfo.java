package com.fortunes.faas.network.model.plate.response;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.mina.core.buffer.IoBuffer;

public class PlateInfo {
	
	/*
	int nAppType 应用的类型，0为没有图片，1为一个近景，2为一近一全 
	int nPeccancyFlag 包用途标志，0-不违章，1-超速，2-闯红灯，3-逆行 
	DWORD dwPacketIndex 包的计数器，包的唯一标志，自动增加 
	char szCarPlate[16] 牌照号码 
	DWORD dwCarPlateColor 牌照颜色 0-蓝牌
	1-黑牌 2-黄牌 3-白牌 4-黄色后牌 5-警车 6-普军
	7-重军 8-武警 9-新白牌 10-武警双层 255-无牌 
	DWORD dwCredit 车牌识别结果置信度，如580、590等 
	DWORD dwPlateTop 车牌矩形框的位置 
	DWORD dwPlateLeft 车牌矩形框的位置 
	DWORD dwPlateWidth 车牌矩形框的位置 
	DWORD dwPlateHeight 车牌矩形框的位置 
	char szVehicleLogo[16] 车标 
	DWORD dwVehicleLength 车长 
	DWORD dwRedLightSTime 红灯开始时间 
	DWORD dwCaptureTime 抓拍时间 
	DWORD dwRedLightETime 红灯结束时间 
	char szPlaceID[8] 地点编号 
	DWORD dwTravelTime 车子经过两个线圈之间的时间，毫秒单位 
	DWORD dwSpeed 车行速度,km/h为单位 
	char szReserved[16] 保留字段 
	DWORD dwMediaType 媒体类型，0为缺省的JPG，1为BMP，2为Video视频段，3为其他定义等 
	DWORD dwImageCount 图象个数 
	--- --- 第一个图象的长度，第二个图象的长度等等，随后是所有的图象数据 */

	
	public static final int TYPE = 0x202;
	
	private String carNo;
	private String carType;
	//private byte[] image1;
	//private byte[] image2;
    private List<byte[]>  images;
    //识别器地址
    private InetSocketAddress identifierAdresss;
	
	public static PlateInfo parse(byte[] contents) {
		IoBuffer b = IoBuffer.allocate(contents.length);
		b.put(contents);
		b.flip();
		b.order(ByteOrder.LITTLE_ENDIAN);
		try {
			FileUtils.writeByteArrayToFile(new File("c:/dump.dat"), b.array());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		long nAppType = b.getUnsignedInt();//4
		long nPeccancyFlag = b.getUnsignedInt();//4
		long dwPacketIndex = b.getUnsignedInt();//4
		byte[] szCarPlate = new byte[16];//16
		b.get(szCarPlate);// 牌照号码 
		long dwCarPlateColor = b.getUnsignedInt();//4
		long dwCredit = b.getUnsignedInt();//4
		System.out.println("dwCredit = "+dwCredit);
		long dwPlateTop = b.getUnsignedInt();//4
		long dwPlateLeft = b.getUnsignedInt();//4
		long dwPlateWidth = b.getUnsignedInt();//4
		long dwPlateHeight = b.getUnsignedInt();//4
		byte[] szVehicleLogo = new byte[16];//车标 //16
		b.get(szVehicleLogo);
		long dwVehicleLength = b.getUnsignedInt();//4
		long dwRedLightSTime = b.getUnsignedInt();//4
		long dwCaptureTime = b.getUnsignedInt();//4
		long dwRedLightETime = b.getUnsignedInt();//4
		byte[] szPlaceID = new byte[8];//地点编号 //8
		b.get(szPlaceID);
		long dwTravelTime = b.getUnsignedInt();//4
		long dwSpeed = b.getUnsignedInt();//4
		byte[] szReserved = new byte[16];//保留 //16
		b.get(szReserved);
		long dwMediaType = b.getUnsignedInt();//4
		long dwImageCount = b.getUnsignedInt();//4
		
		//取图片长度
		long imageLengths[]=new long[(int) dwImageCount];
		for (int i = 0; i < dwImageCount; i++) {
			imageLengths[i]= b.getUnsignedInt();
		}
		
		PlateInfo info = new PlateInfo();
		
		//取图片数据
		List<byte[]> imageList=new ArrayList<byte[]>();
		for (int i = 0; i < dwImageCount; i++) {
			/*if(i==0){
				byte[] image1Bytes = new byte[(int) imageLengths[i]];
				b.get(image1Bytes);
				info.setImage1(image1Bytes);
			}else if(i==1){
				byte[] image2Bytes = new byte[(int)imageLengths[i]];
				b.get(image2Bytes);
				info.setImage2(image2Bytes);
			}*/
			byte[] imageBytes = new byte[(int) imageLengths[i]];
			b.get(imageBytes);
			imageList.add(imageBytes);
		}
		info.setImages(imageList);
		
		try {
			String carNo = new String(szCarPlate,"GBK").trim();
			info.setCarNo(carNo);
			info.setCarType(dwCarPlateColor+"");
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return info;
	}

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public void setImages(List<byte[]> images) {
		this.images = images;
	}

	public List<byte[]> getImages() {
		return images;
	}

	public void setIdentifierAdresss(InetSocketAddress identifierAdresss) {
		this.identifierAdresss = identifierAdresss;
	}

	public InetSocketAddress getIdentifierAdresss() {
		return identifierAdresss;
	}


}
