import time
import json
import paho.mqtt.client as mqtt

#MQTT broker beállításai
MQTT_BROKER = "CHANGE_ME"
MQTT_PORT = CHANGE_ME
MQTT_TOPIC_TEMP_HUM = "zigbee2mqtt/Temp-Hum"  #Hőmérséklet és páratartalom szenzor
MQTT_TOPIC_SOIL = "zigbee2mqtt/SoilS"         #Talajnedvesség szenzor
MQTT_TOPIC_VALVE = "zigbee2mqtt/Valve/set"    #Okos csap

#Öntözés indulásának feltételei (Gyep)
TEMPERATURE_MIN = 15
TEMPERATURE_MAX = 28
SOIL_MOISTURE_MIN = 50
is_watering = False
watering_duration = 480  #Öntözés időtartama másodpercben (8 perc)

#MQTT
client = mqtt.Client()
temperature = None
soil_moisture = None

#Vezérlése
def start_watering():
    global is_watering
    is_watering = True
    print("Öntözőrendszer BEKAPCSOLVA.")
    client.publish(MQTT_TOPIC_VALVE, json.dumps({"state": "ON"}))

def stop_watering():
    global is_watering
    is_watering = False
    print("Öntözőrendszer KIKAPCSOLVA.")
    client.publish(MQTT_TOPIC_VALVE, json.dumps({"state": "OFF"}))

#Feltételek ellenőrzése
def check_conditions():
    global temperature, soil_moisture, is_watering
    if temperature is not None and soil_moisture is not None:
        if (TEMPERATURE_MIN < temperature < TEMPERATURE_MAX) and soil_moisture < SOIL_MOISTURE_MIN:
            if not is_watering:
                start_watering()
                time.sleep(watering_duration)
                stop_watering()
        elif is_watering:
            stop_watering()

# MQTT üzenet fogadás
def on_message(client, userdata, msg):
    global temperature, soil_moisture
    topic = msg.topic
    payload = msg.payload.decode("utf-8")
    data = json.loads(payload)

    if topic == MQTT_TOPIC_TEMP_HUM:
        temperature = data.get("temperature")
        print(f"Hőmérséklet frissítve: {temperature} °C")
    elif topic == MQTT_TOPIC_SOIL:
        soil_moisture = data.get("soil_moisture")
        print(f"Talajnedvesség frissítve: {soil_moisture} %")

    check_conditions()

# MQTT kapcsolódás eseménykezelése
def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Csatlakozás a brokerhez: Sikeres!. :) ")
        client.subscribe(MQTT_TOPIC_TEMP_HUM)
        client.subscribe(MQTT_TOPIC_SOIL)
    else:
        print("Csatlakozás a bokerhez: Sikertelen!. :( \n")
        print("Hibakód: ", rc)


client.on_connect = on_connect
client.on_message = on_message

#MQTT kapcsolódás (újrapróbálozásokkal)
while True:
    try:
        print("Csatlakozás az MQTT brokerhez...")
        client.connect(MQTT_BROKER, MQTT_PORT, 60)
        break #Ha sikerült a kapcsolódás
    except Exception as e:
        print(f"Kapcsolódási hiba: {e}. Újrapróbálkozás 10 másodperc múlva.")
        time.sleep(10)

client.loop_forever()
