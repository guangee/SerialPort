/*
  Blink
  Turns on an LED on for one second, then off for one second, repeatedly.

  Most Arduinos have an on-board LED you can control. On the Uno and
  Leonardo, it is attached to digital pin 13. If you're unsure what
  pin the on-board LED is connected to on your Arduino model, check
  the documentation at http://arduino.cc

  This example code is in the public domain.

  modified 8 May 2014
  by Scott Fitzgerald
 */


// the setup function runs once when you press reset or power the board
void setup() {
  // initialize digital pin 13 as an output.
  pinMode(13, OUTPUT);

  Serial.begin(9600);
}
int control = 0;
int lampOne = 0;
int lampTwo = 0;
int lampThree = 0;
int FanOne = 0;
// the loop function runs over and over again forever
void loop() {
  digitalWrite(13, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(300);              // wait for a second
  digitalWrite(13, LOW);    // turn the LED off by making the voltage LOW
  delay(600);              // wait for a second

  //begin

  if (Serial.available() > 0) {
    control = Serial.read() - 'A';
    Serial.flush();

    Serial.println(control, DEC);
    lampOne = (control >> 3) % 2;
    lampTwo = (control >> 2) % 2;
    lampThree = (control >> 1) % 2;
    FanOne = control % 2;

    //Serial.print(lampOne ? "lampOne on " : "lampOne off ");
    //Serial.print(lampTwo ? "lampTwo on " : "lampTwo off ");
    //Serial.print(lampThree ? "lampThree on " : "lampThree off ");
    //Serial.println(FanOne ? "FanOne on " : "FanOne off ");
    Serial.flush();
  }
  //temperature-humidity
  float h = dht.readHumidity();//读湿度
    float t = dht.readTemperature();//读温度，默认为摄氏度
    float h1 = h*100;
    float t1 = t*100;
    long h2 = (long) h1;
    long t2 = (long) t1;
    long data=h2*10000+t2;
    Serial.println(data, DEC);

}