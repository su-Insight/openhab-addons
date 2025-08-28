# The Keys Binding

This is the binding for [TheKeys Smartlock](https://www.the-keys.eu/en/produits/8-serrure-connectee.html).
This binding allows you to integrate, view, control and configure TheKeys Gateway and TheKeys Smartlock.

![The Keys smartlock](doc/thekeys-smartlock.png)
![The Keys gateway](doc/thekeys-gateway.png)

## Prerequisites

At least one TheKeys Smartlock which is associated with The Keys Gateway.
The Gateway must be paired with the Smartlock via the android/ios app.
It is recommended to configure static IP addresses for the Gateway.

### Setup

- Create access for the Gateway from the app or from https://api.the-keys.fr
- Go to the shares section of the lock (https://api.the-keys.fr/en/compte/serrure)
- In the accessory shares table, get the code of your Gateway

## Supported Bridges

### `gateway` Bridge Thing Configuration

This binding supports just one bridge type: The Keys Gateway (`thekeys:gateway`).
The following configuration options are available:

| Name            | Type    | Description                             | Default | Required | Advanced |
|-----------------|---------|-----------------------------------------|---------|----------|----------|
| host            | text    | Host or IP address of the gateway       | N/A     | yes      | no       |
| code            | text    | The gateway access code                 | N/A     | yes      | no       |
| refreshInterval | integer | Interval the device is polled in sec    | 5       | no       | no       |
| apiTimeout      | integer | API timeout in seconds for each request | 30      | no       | yes      |

### Discovery

The bridge can't be discovered automatically.
The `host` parameter must be provided.
You can find it from the ios/android app : Home Page > Accessories > TK Gateway > Configure > IP

### Supported Channels

The gateway has no channels.
It only exposes one property `firmwareVersion` containing the firmware version.

## Supported Things

### `smartlock` Thing Configuration

This thing allows to control the smartlock and check its state.

| Name            | Type    | Description                           | Default | Required | Advanced |
|-----------------|---------|---------------------------------------|---------|----------|----------|
| lockId          | integer | Identifier of the smartlock           | N/A     | yes      | no       |

### Discovery

The smartlock can be discovered when the Gateway is configured and online.
Hit the "scan" button when you add the thing through the UI

### Supported Channels

| Channel         | Type         | Read/Write | Description                                    |
|-----------------|--------------|------------|------------------------------------------------|
| status          | String       | R          | Status of the smartlock                        |
| batteryLevel    | Number:Power | R          | Current battery level                          |
| lowBattery      | Switch       | R          | Low battery warning                            |
| rssi            | Number       | R          | Bluetooth signal strength with the gateway     |
| position        | Number       | R          | Position of the lock                           |
| syncInProgress  | Switch       | R          | Indicates the pending update of the lock state |
| lastSync        | DateTime     | R          | Date of the last success sync with the lock    |
| lock            | Switch       | RW         | Switch to open and close the lock              |

## Full Example

A manual setup through files could look like this:

### things/thekeys.things

```
Bridge thekeys:gateway:tk-gateway [ host="192.168.1.50", code="secretcode", refreshInterval=5, apiTimeout=30 ] {
    Thing smartlock 1234 [ lockId=1234 ]
}
```

### items/thekeys.item

```
// Equipment representing thing
Group Smartlock "Smartlock" ["Equipment"]

// Points
String       Smartlock_Lock_Status                 "Lock Status"                                    (Smartlock) ["Point"]                  { channel="thekeys:smartlock:tk-gateway:1234:status" }         
Number       Smartlock_Battery_Level               "Battery Level"               <Battery>          (Smartlock) ["Measurement", "Energy"]  { channel="thekeys:smartlock:tk-gateway:1234:batteryLevel" }   
Switch       Smartlock_Low_Battery                 "Low Battery"                 <LowBattery>       (Smartlock) ["Energy", "LowBattery"]   { channel="thekeys:smartlock:tk-gateway:1234:lowBattery" }     
Number:Power Smartlock_Bluetooth_RSSI              "Bluetooth RSSI"              <QualityOfService> (Smartlock) ["Point"]                  { channel="thekeys:smartlock:tk-gateway:1234:rssi" }           
Number       Smartlock_Smartlock_Position          "Smartlock Position"                             (Smartlock) ["Point"]                  { channel="thekeys:smartlock:tk-gateway:1234:position" }       
Switch       Smartlock_Synchronization_In_Progress "Synchronization In Progress"                    (Smartlock) ["Point"]                  { channel="thekeys:smartlock:tk-gateway:1234:syncInProgress" } 
DateTime     Smartlock_Last_Sync                   "Last Sync"                                      (Smartlock) ["Point"]                  { channel="thekeys:smartlock:tk-gateway:1234:lastSync" }       
Switch       Smartlock_Lock                        "Lock"                        <Lock>             (Smartlock) ["Point"]                  { channel="thekeys:smartlock:tk-gateway:1234:lock" }                      
```
