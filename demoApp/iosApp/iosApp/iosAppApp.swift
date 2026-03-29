import SwiftUI
import sharedUI

@main
struct iosAppApp: App {
    init() {
        MainViewControllerKt.initializeLoKdroid()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
