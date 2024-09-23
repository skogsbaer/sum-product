//
//  medicationApp.swift
//  medication
//
//  Created by Michael Sperber on 22.09.24.
//

import SwiftUI

enum LookupError: Error {
    case capitalNotFound
    case populationNotFound
}
enum PopulationResult {
    case success(Int)
    case error(LookupError)
}

enum Dosage {
    case Tablet(Int, Int, Int)
    case Infusion(Double, Int)
}

extension Dosage {
    func format() -> String {
        return switch self {
        case let .Tablet(morning, midday, evening):
            morning.formatted() + "-" + midday.formatted() + "-" + evening
        case let .Infusion(speed, duration):
            speed.formatted() + "ml/min for " + duration.formatted() + "h"
        }
        
    }
}

@main
struct medicationApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
