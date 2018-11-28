// Incluímos la librería para poder controlar el servo
#include <Servo.h>
#include <Wire.h> //include Wire.h library
#include "RTClib.h" //include Adafruit RTC library
#include <EEPROM.h> // libreria para la memoria del arduino
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

RTC_DS3231 rtc; //Make a RTC DS3231 object

// Servo
Servo servoMotorA;
Servo servoMotorB;
int pinServoA = 9;
int pinServoB = 10;

//Infrarrojos
int pinInfraA = 4;
int pinInfraB = 5;

//LDR
int pinLDR = A0;

//LEDS
int pinLedPwd = 6;
int pinLedLuz = 7;

//Boton
//int pinBoton = 8;
int pinBoton = A1;
int pinBoton2 = A3;

//BUZZER
int pinBuzzer = 13;

//Led Infrarrojo
int pinLedInfra = 12;

//Variable Globales
boolean hayQueGirarA = false;
boolean girandoA = false;
long tiempoGirandoA = 0;
boolean hayQueGirarB = false;
boolean girandoB = false;
long tiempoGirandoB = 0;
int intensidadLedAlarma = 0;
int direccionIntensidad = 1;
int ldrLecturasSeguidasHigh = 0;
int ldrLecturasSeguidasLow = 0;

//Variables planificacion
int TAM_PLANIF = 7;
char planif[7] = "";
char result[7] = "";
char contador[7] = "0";
int cont = 0;
int cant_planif = 0;
int pos = 0;
char tolva;
int dia;
char aux[2];
char hora[3];
char minuto[3];
int minutoAnterior = 0;
//Set the names of days
char daysOfTheWeek[7][12] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
boolean flagTA = false, flagTB = false;

void setup() {
  // Iniciamos el monitor serie para mostrar el resultado
  Serial.begin(9600);

  //Print the message if RTC is not available
  if (! rtc.begin()) {
    Serial.println("Couldn't find RTC");
    while (1);
  }
  //Setup of time if RTC lost power or time is not set
  if (rtc.lostPower()) {
    //Sets the code compilation time to RTC DS3231
    rtc.adjust(DateTime(F(__DATE__), F(__TIME__)));
  }

  //Infrarrojos
  pinMode(pinInfraA, INPUT);
  pinMode(pinInfraB, INPUT);

  //LDR
  pinMode(pinLDR, INPUT);

  //LEDS
  pinMode(pinLedPwd, OUTPUT);
  pinMode(pinLedLuz, OUTPUT);
  pinMode(pinLedInfra, OUTPUT);

  //Boton
  pinMode(pinBoton, INPUT);

  // Servo
  servoMotorA.attach(pinServoA);
  servoMotorA.write(90);
  servoMotorB.attach(pinServoB);
  servoMotorB.write(90);

  DateTime now = rtc.now();
  minutoAnterior = now.minute() + 1;
}

void loop() {

  //Set now as RTC time
  DateTime now = rtc.now();

  // verifica las planificaciones
  pos = 0;
  EEPROM.get(0, contador);
  cont = atoi(contador);
  //  Serial.print("CONTADOR: ");
  //  Serial.println(cont);
  for (int i = 0; i < cont; i++) {
    pos += TAM_PLANIF; // me salteo el contador
    EEPROM.get(pos, result);
    //Serial.println(result);
    tolva = result[0];
    aux[0] = result[1];
    aux[1] = '\0';
    dia = atoi(aux);
    hora[0] = result[2];
    hora[1] = result[3];
    hora[2] = '\0';
    minuto[0] = result[4];
    minuto[1] = result[5];
    minuto[2] = '\0';

    if (dia == 7)
      dia = 0;

    if (minutoAnterior != now.minute() + 1) {
      flagTA = false;
      flagTB = false;
      minutoAnterior = now.minute() + 1;
    }
    if (dia == now.dayOfTheWeek()) {
      if (now.hour() == atoi(hora)) {
        if ((now.minute() + 1) == atoi(minuto)) {
          if (tolva == '1') {
            if (flagTA == false) {
              Serial.println("DESPEDIR PASTILLA DE LA TOLVA");
              Serial.println(tolva);
              flagTA = true;
              hayQueGirarA = true;
            }
          }
          if (tolva == '2') {
            if (flagTB == false) {
              Serial.println("DESPEDIR PASTILLA DE LA TOLVA");
              Serial.println(tolva);
              flagTB = true;
              hayQueGirarB = true;
            }
          }
        }
      }
    }
  }

  //SERVO A
  if (hayQueGirarA) {
    if (!girandoA) {
      girandoA = true;
      tiempoGirandoA = millis();
      //Maxima velocidad Adelante
      servoMotorA.write(180);
    } else {
      if ((millis() - tiempoGirandoA)  >= 450 ) {
        servoMotorA.write(90);
        girandoA = false;
        hayQueGirarA = false;
      }
    }
  }

  //SERVO B
  if (hayQueGirarB) {
    if (!girandoB) {
      girandoB = true;
      tiempoGirandoB = millis();
      //Maxima velocidad Adelante
      servoMotorB.write(180);
    } else {
      if ((millis() - tiempoGirandoB)  >= 450 ) {
        servoMotorB.write(90);
        girandoB = false;
        hayQueGirarB = false;
      }
    }
  }

  //BLUETOOTH
  if (Serial.available()) {
    //    String lectura = "";
    char lectura = Serial.read();
    //Serial.print("Dato recibido: ");
    //Serial.println(lectura);
    if (lectura == 'a') {
      hayQueGirarA = true;
      //Serial.print("Gira A");
      Serial.flush();
    } else if (lectura == 'b') {
      hayQueGirarB = true;
      //Serial.print("Gira B");
      Serial.flush();
    }
    if (lectura == 'x') {
      pos = 0;
      cont = 0;
      strcpy(planif, "");
      strcpy(contador, "0");
      EEPROM.put(0, contador);
    }

    if (lectura == 'c') {
      tone(pinBuzzer, 440, 250);
      //Serial.print("Suena Alarma");
      Serial.flush();
    } else if (lectura == 'p') {
      cont++;
      itoa(cont, contador, 10);
      EEPROM.put(0, contador);
      Serial.flush();
      lectura = Serial.read();
      int k = 0;
      while (Serial.available() && lectura != '\n') {
        planif[k] = lectura;
        //Serial.print(planif[k]);
        lectura = Serial.read();
        k++;
      }
      planif[7] = '\0';
      pos += TAM_PLANIF;
      EEPROM.put(pos, planif);
      EEPROM.get(pos, result);
      EEPROM.get(0, contador);
      Serial.flush();
    }
  }

  //INFRARROJO B - BUZZER - LED PWD
  int value = 0;
  value = digitalRead(pinInfraB);  //lectura digital de pin
  if (value == LOW) {
    //Si detecta obstáculo enciende buzzer y led pwd
    tone(pinBuzzer, 440, 250);
    Serial.println(intensidadLedAlarma);
    analogWrite(pinLedPwd, intensidadLedAlarma);
    intensidadLedAlarma += direccionIntensidad * 1;
    if (intensidadLedAlarma >= 256 || intensidadLedAlarma <= 0) {
      direccionIntensidad *= -1;
    }
  } else {
    //No detecta obstáculo apaga el Led pwd
    digitalWrite(pinLedPwd, LOW);
  }

  //INFRARROJO A
  int value1 = 0;
  value1 = digitalRead(pinInfraA);  //lectura digital de pin
  if (value1 == LOW) {
    digitalWrite(pinLedInfra, LOW);
  } else {
    digitalWrite(pinLedInfra, HIGH);
  }

  //LDR
  //Serial.println(analogRead(pinLDR));
  if (analogRead(pinLDR) <= 110) {
    if (ldrLecturasSeguidasHigh == 3515) {
      digitalWrite(pinLedLuz, HIGH);
      ldrLecturasSeguidasHigh = 0;
    } else {
      ldrLecturasSeguidasHigh++;
      ldrLecturasSeguidasLow = 0;
    }
  } else {
    if (ldrLecturasSeguidasLow == 3515) {
      digitalWrite(pinLedLuz, LOW);
      ldrLecturasSeguidasLow = 0;
    } else {
      ldrLecturasSeguidasLow++;
      ldrLecturasSeguidasHigh = 0;
    }
  }

  //Boton
  //Serial.println("BOTON 1");
  //Serial.println(analogRead(pinBoton));
  if (analogRead(pinBoton) >= 930) {
    //Serial.println(analogRead(pinBoton));
    hayQueGirarA = true;
    //Serial.println("BOTON");
  }

}
