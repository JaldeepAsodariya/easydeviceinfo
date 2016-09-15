/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.nisrulz.projecteasydeviceinfo;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import github.nisrulz.easydeviceinfo.ads.EasyAdsMod;
import github.nisrulz.easydeviceinfo.base.EasyAppMod;
import github.nisrulz.easydeviceinfo.base.EasyBatteryMod;
import github.nisrulz.easydeviceinfo.base.EasyBluetoothMod;
import github.nisrulz.easydeviceinfo.base.EasyConfigMod;
import github.nisrulz.easydeviceinfo.base.EasyCpuMod;
import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;
import github.nisrulz.easydeviceinfo.base.EasyDisplayMod;
import github.nisrulz.easydeviceinfo.base.EasyIdMod;
import github.nisrulz.easydeviceinfo.base.EasyLocationMod;
import github.nisrulz.easydeviceinfo.base.EasyMemoryMod;
import github.nisrulz.easydeviceinfo.base.EasyNetworkMod;
import github.nisrulz.easydeviceinfo.base.EasyNfcMod;
import github.nisrulz.easydeviceinfo.base.EasySimMod;
import github.nisrulz.easydeviceinfo.common.EasyDeviceInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private ArrayAdapter<String> adapter;

  @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //Data Array List of Info Object
    final ArrayList<String> data = new ArrayList<>();

    //Add Data
    HashMap<String, String> deviceDataMap = new HashMap<>();

    // Library Info
    EasyDeviceInfo easyDeviceInfo = new EasyDeviceInfo("na");
    data.add(easyDeviceInfo.getLibraryVersion());

    // ID Mod
    EasyIdMod easyIdMod = new EasyIdMod(this);

    String[] emailIds = easyIdMod.getAccounts();
    StringBuilder emailString = new StringBuilder();
    if (emailIds != null && emailIds.length > 0) {
      for (String e : emailIds) {
        emailString.append(e).append("\n");
      }
    }
    else {
      emailString.append("-");
    }

    EasyAdsMod easyAdsMod = new EasyAdsMod(this);
    easyAdsMod.getAndroidAdId(new EasyAdsMod.AdIdentifierCallback() {
      @Override
      public void onSuccess(String adIdentifier, boolean adDonotTrack) {
        // Add Data
        data.add("Android Advertiser ID :" + adIdentifier);
        data.add("Ad Do not Track :" + String.valueOf(adDonotTrack));
        adapter.notifyDataSetChanged();
      }
    });

    // Config Mod
    EasyConfigMod easyConfigMod = new EasyConfigMod(this);
    deviceDataMap.put("Time (ms)", String.valueOf(easyConfigMod.getTime()));
    deviceDataMap.put("Formatted Time (24Hrs)", easyConfigMod.getFormattedTime());
    deviceDataMap.put("UpTime (ms)", String.valueOf(easyConfigMod.getUpTime()));
    deviceDataMap.put("Formatted Up Time (24Hrs)", easyConfigMod.getFormattedUpTime());
    deviceDataMap.put("Date", String.valueOf(easyConfigMod.getCurrentDate()));
    deviceDataMap.put("Formatted Date", easyConfigMod.getFormattedDate());
    deviceDataMap.put("SD Card available", String.valueOf(easyConfigMod.hasSdCard()));
    deviceDataMap.put("Running on emulator", String.valueOf(easyConfigMod.isRunningOnEmulator()));
    switch (easyConfigMod.getDeviceRingerMode()) {
      case EasyConfigMod.RINGER_MODE_NORMAL:
        deviceDataMap.put("Ringer mode", "normal");
        break;
      case EasyConfigMod.RINGER_MODE_VIBRATE:
        deviceDataMap.put("Ringer mode", "vibrate");
        break;
      case EasyConfigMod.RINGER_MODE_SILENT:
        deviceDataMap.put("Ringer mode", "silent");
        break;
      default:
        //do nothing
        break;
    }

    // SIM Mod
    EasySimMod easySimMod = new EasySimMod(this);
    deviceDataMap.put("IMSI", easySimMod.getIMSI());
    deviceDataMap.put("SIM Serial Number", easySimMod.getSIMSerial());
    deviceDataMap.put("Country", easySimMod.getCountry());
    deviceDataMap.put("Carrier", easySimMod.getCarrier());
    deviceDataMap.put("SIM Network Locked", String.valueOf(easySimMod.isSimNetworkLocked()));
    deviceDataMap.put("Is Multi SIM", String.valueOf(easySimMod.isMultiSim()));
    deviceDataMap.put("Number of active SIM", String.valueOf(easySimMod.getNumberOfActiveSim()));

    List<SubscriptionInfo> activeMultiSimInfo = easySimMod.getActiveMultiSimInfo();
    if (activeMultiSimInfo != null) {
      StringBuilder stringBuilder = new StringBuilder();
      for (int i = 0; i < activeMultiSimInfo.size(); i++) {
        stringBuilder.append("\nSIM " + i + " Info")
            .append("\nPhone Number :")
            .append(activeMultiSimInfo.get(i).getNumber())
            .append("\n")
            .append("Carrier Name :")
            .append(activeMultiSimInfo.get(i).getCarrierName())
            .append("\n")
            .append("Country :")
            .append(activeMultiSimInfo.get(i).getCountryIso())
            .append("\n")
            .append("Roaming :")
            .append(activeMultiSimInfo.get(i).getDataRoaming()
                == SubscriptionManager.DATA_ROAMING_ENABLE ? true : false)
            .append("\n")
            .append("Display Name :")
            .append(activeMultiSimInfo.get(i).getDisplayName())
            .append("\n")
            .append("Sim Slot  :")
            .append(activeMultiSimInfo.get(i).getSimSlotIndex())
            .append("\n");
      }
      deviceDataMap.put("Multi SIM Info", stringBuilder.toString());
    }

    // Device Mod
    EasyDeviceMod easyDeviceMod = new EasyDeviceMod(this);
    deviceDataMap.put("Language", easyDeviceMod.getLanguage());
    deviceDataMap.put("Android ID", easyIdMod.getAndroidID());
    deviceDataMap.put("IMEI", easyDeviceMod.getIMEI());
    deviceDataMap.put("User-Agent", easyIdMod.getUA());
    deviceDataMap.put("GSF ID", easyIdMod.getGSFID());
    deviceDataMap.put("Pseudo ID", easyIdMod.getPseudoUniqueID());
    deviceDataMap.put("Device Serial", easyDeviceMod.getSerial());
    deviceDataMap.put("Manufacturer", easyDeviceMod.getManufacturer());
    deviceDataMap.put("Model", easyDeviceMod.getModel());
    deviceDataMap.put("OS Codename", easyDeviceMod.getOSCodename());
    deviceDataMap.put("OS Version", easyDeviceMod.getOSVersion());
    deviceDataMap.put("Display Version", easyDeviceMod.getDisplayVersion());
    deviceDataMap.put("Phone Number", easyDeviceMod.getPhoneNo());
    deviceDataMap.put("Radio Version", easyDeviceMod.getRadioVer());
    deviceDataMap.put("Product ", easyDeviceMod.getProduct());
    deviceDataMap.put("Device", easyDeviceMod.getDevice());
    deviceDataMap.put("Board", easyDeviceMod.getBoard());
    deviceDataMap.put("Hardware", easyDeviceMod.getHardware());
    deviceDataMap.put("BootLoader", easyDeviceMod.getBootloader());
    deviceDataMap.put("Device Rooted", String.valueOf(easyDeviceMod.isDeviceRooted()));
    deviceDataMap.put("Fingerprint", easyDeviceMod.getFingerprint());
    deviceDataMap.put("Build Brand", easyDeviceMod.getBuildBrand());
    deviceDataMap.put("Build Host", easyDeviceMod.getBuildHost());
    deviceDataMap.put("Build Tag", easyDeviceMod.getBuildTags());
    deviceDataMap.put("Build Time", String.valueOf(easyDeviceMod.getBuildTime()));
    deviceDataMap.put("Build User", easyDeviceMod.getBuildUser());
    deviceDataMap.put("Build Version Release", easyDeviceMod.getBuildVersionRelease());
    deviceDataMap.put("Screen Display ID", easyDeviceMod.getScreenDisplayID());
    deviceDataMap.put("Build Version Codename", easyDeviceMod.getBuildVersionCodename());
    deviceDataMap.put("Build Version Increment", easyDeviceMod.getBuildVersionIncremental());
    deviceDataMap.put("Build Version SDK", String.valueOf(easyDeviceMod.getBuildVersionSDK()));
    deviceDataMap.put("Build ID", easyDeviceMod.getBuildID());
    switch (easyDeviceMod.getDeviceType(this)) {
      case EasyDeviceMod.DEVICE_TYPE_WATCH:
        deviceDataMap.put("Device type", "watch");
        break;
      case EasyDeviceMod.DEVICE_TYPE_PHONE:
        deviceDataMap.put("Device type", "phone");
        break;
      case EasyDeviceMod.DEVICE_TYPE_PHABLET:
        deviceDataMap.put("Device type", "phablet");
        break;
      case EasyDeviceMod.DEVICE_TYPE_TABLET:
        deviceDataMap.put("Device type", "tablet");
        break;
      case EasyDeviceMod.DEVICE_TYPE_TV:
        deviceDataMap.put("Device type", "tv");
        break;
      default:
        //do nothing
        break;
    }

    switch (easyDeviceMod.getPhoneType()) {
      case EasyDeviceMod.PHONE_TYPE_CDMA:
        deviceDataMap.put("Phone Type", "CDMA");
        break;
      case EasyDeviceMod.PHONE_TYPE_GSM:
        deviceDataMap.put("Phone Type", "GSM");
        break;
      case EasyDeviceMod.PHONE_TYPE_NONE:
        deviceDataMap.put("Phone Type", "None");
        break;
      default:
        deviceDataMap.put("Phone Type", "Unknown");
        break;
    }

    switch (easyDeviceMod.getOrientation(this)) {
      case EasyDeviceMod.ORIENTATION_LANDSCAPE:
        deviceDataMap.put("Orientation", "Landscape");
        break;
      case EasyDeviceMod.ORIENTATION_PORTRAIT:
        deviceDataMap.put("Orientation", "Portrait");
        break;
      case EasyDeviceMod.ORIENTATION_UNKNOWN:
        deviceDataMap.put("Orientation", "Unknown");
        break;
      default:
        deviceDataMap.put("Orientation", "Unknown");
        break;
    }

    // App Mod
    EasyAppMod easyAppMod = new EasyAppMod(this);
    deviceDataMap.put("Installer Store", easyAppMod.getStore());
    deviceDataMap.put("App Name", easyAppMod.getAppName());
    deviceDataMap.put("Package Name", easyAppMod.getPackageName());
    deviceDataMap.put("Activity Name", easyAppMod.getActivityName());
    deviceDataMap.put("App version", easyAppMod.getAppVersion());
    deviceDataMap.put("App versioncode", easyAppMod.getAppVersionCode());
    deviceDataMap.put("Does app have Camera permission?",
        String.valueOf(easyAppMod.isPermissionGranted(Manifest.permission.CAMERA)));

    //Network Mod
    EasyNetworkMod easyNetworkMod = new EasyNetworkMod(this);
    deviceDataMap.put("WIFI MAC Address", easyNetworkMod.getWifiMAC());
    deviceDataMap.put("IPv4 Address", easyNetworkMod.getIPv4Address());
    deviceDataMap.put("IPv6 Address", easyNetworkMod.getIPv6Address());
    deviceDataMap.put("Network Available", String.valueOf(easyNetworkMod.isNetworkAvailable()));
    deviceDataMap.put("Wi-Fi enabled", String.valueOf(easyNetworkMod.isWifiEnabled()));
    switch (easyNetworkMod.getNetworkType()) {
      case EasyNetworkMod.CELLULAR_UNKNOWN:
        deviceDataMap.put("Network Type", "Unknown");
        break;
      case EasyNetworkMod.CELLULAR_UNIDENTIFIED_GEN:
        deviceDataMap.put("Network Type", "Cellular Unidentified Generation");
        break;
      case EasyNetworkMod.CELLULAR_2G:
        deviceDataMap.put("Network Type", "Cellular 2G");
        break;
      case EasyNetworkMod.CELLULAR_3G:
        deviceDataMap.put("Network Type", "Cellular 3G");
        break;
      case EasyNetworkMod.CELLULAR_4G:
        deviceDataMap.put("Network Type", "Cellular 4G");
        break;
      default:
        // Do nothing
        break;
    }

    // Battery Mod
    EasyBatteryMod easyBatteryMod = new EasyBatteryMod(this);
    deviceDataMap.put("Battery Percentage",
        String.valueOf(easyBatteryMod.getBatteryPercentage()) + "%");
    deviceDataMap.put("Is device charging", String.valueOf(easyBatteryMod.isDeviceCharging()));
    deviceDataMap.put("Battery present", String.valueOf(easyBatteryMod.isBatteryPresent()));
    deviceDataMap.put("Battery technology", String.valueOf(easyBatteryMod.getBatteryTechnology()));
    deviceDataMap.put("Battery temperature",
        String.valueOf(easyBatteryMod.getBatteryTemperature()) + " deg C");
    deviceDataMap.put("Battery voltage",
        String.valueOf(easyBatteryMod.getBatteryVoltage()) + " mV");
    switch (easyBatteryMod.getBatteryHealth()) {
      case EasyBatteryMod.HEALTH_GOOD:
        deviceDataMap.put("Battery health", "Good");
        break;
      case EasyBatteryMod.HEALTH_HAVING_ISSUES:
        deviceDataMap.put("Battery health", "Having issues");
        break;
      default:
        deviceDataMap.put("Battery health", "Having issues");
        break;
    }

    switch (easyBatteryMod.getChargingSource()) {
      case EasyBatteryMod.CHARGING_VIA_AC:
        deviceDataMap.put("Device charging via ", "AC");
        break;
      case EasyBatteryMod.CHARGING_VIA_USB:
        deviceDataMap.put("Device charging via ", "USB");
        break;
      case EasyBatteryMod.CHARGING_VIA_WIRELESS:
        deviceDataMap.put("Device charging via ", "Wireless");
        break;
      case EasyBatteryMod.CHARGING_VIA_UNKNOWN_SOURCE:
        deviceDataMap.put("Device charging via ", "Unknown Source");
        break;
      default:
        deviceDataMap.put("Device charging via ", "Unknown Source");
        break;
    }

    //Bluetooth Mod
    EasyBluetoothMod easyBluetoothMod = new EasyBluetoothMod(this);
    deviceDataMap.put("BT MAC Address", easyBluetoothMod.getBluetoothMAC());

    // Display Mod
    EasyDisplayMod easyDisplayMod = new EasyDisplayMod(this);
    deviceDataMap.put("Display Resolution", easyDisplayMod.getResolution());
    deviceDataMap.put("Screen Density", easyDisplayMod.getDensity());

    deviceDataMap.put("Email ID", emailString.toString());

    // Location Mod
    EasyLocationMod easyLocationMod = new EasyLocationMod(this);
    double[] l = easyLocationMod.getLatLong();
    String lat = String.valueOf(l[0]);
    String lon = String.valueOf(l[1]);
    deviceDataMap.put("Latitude", lat);
    deviceDataMap.put("Longitude", lon);

    // Memory Mod
    EasyMemoryMod easyMemoryMod = new EasyMemoryMod(this);
    deviceDataMap.put("Total RAM",
        String.valueOf(easyMemoryMod.convertToGb(easyMemoryMod.getTotalRAM())) + " Gb");
    deviceDataMap.put("Available Internal Memory",
        String.valueOf(easyMemoryMod.convertToGb(easyMemoryMod.getAvailableInternalMemorySize()))
            + " Gb");
    deviceDataMap.put("Available External Memory",
        String.valueOf(easyMemoryMod.convertToGb(easyMemoryMod.getAvailableExternalMemorySize()))
            + " Gb");
    deviceDataMap.put("Total Internal Memory",
        String.valueOf(easyMemoryMod.convertToGb(easyMemoryMod.getTotalInternalMemorySize()))
            + " Gb");
    deviceDataMap.put("Total External memory",
        String.valueOf(easyMemoryMod.convertToGb(easyMemoryMod.getTotalExternalMemorySize()))
            + " Gb");

    // CPU Mod
    EasyCpuMod easyCpuMod = new EasyCpuMod();
    deviceDataMap.put("Supported ABIS", easyCpuMod.getStringSupportedABIS());
    deviceDataMap.put("Supported 32 bit ABIS", easyCpuMod.getStringSupported32bitABIS());
    deviceDataMap.put("Supported 64 bit ABIS", easyCpuMod.getStringSupported64bitABIS());

    // NFC Mod
    EasyNfcMod easyNfcMod = new EasyNfcMod(this);
    deviceDataMap.put("is NFC present", String.valueOf(easyNfcMod.isNfcPresent()));
    deviceDataMap.put("is NFC enabled", String.valueOf(easyNfcMod.isNfcEnabled()));

    for (String key : deviceDataMap.keySet()) {
      data.add(key + " : " + deviceDataMap.get(key));
    }

    ListView lv = (ListView) findViewById(R.id.listview);
    adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
    lv.setAdapter(adapter);
  }
}
