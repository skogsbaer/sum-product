namespace Library

type dosage 
  = tablet of int * int * int
  | infusion of double * double



module Say =
    let hello name =
        printfn "Hello %s" name
