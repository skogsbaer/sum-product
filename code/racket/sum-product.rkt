#lang deinprogramm/sdp
(define-record tablet
  make-tablet
  (tablet-morning natural)
  (tablet-midday natural)
  (tablet-evening natural))

(define-record infusion
  make-infusion
  (infusion-speed rational)
  (infusion-duration natural))

(define dosage
  (signature (mixed tablet infusion)))

(: format (dosage -> string))

(define format
  (lambda (dosage)
    (match dosage
      ((make-tablet morning midday evening)
       (string-append (number->string morning) "-"
                      (number->string midday) "-"
                      (number->string evening)))
      ((make-infusion speed duration)
       (string-append
        (number->string speed) "ml/min for "
        (number->string duration) "h")))))

(format (make-tablet 1 2 3))
(format (make-infusion 1.5 3))