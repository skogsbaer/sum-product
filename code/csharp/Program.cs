var paracetamol = new Medication("Paracetamol", new Dosage.Tablet(1,0,2));
var infliximab = new Medication("Infliximab", new Dosage.Infusion(1.5, 2));

Console.WriteLine(paracetamol.format());
Console.WriteLine(infliximab.format());

