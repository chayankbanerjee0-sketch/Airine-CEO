# Airline CEO: Global Aviation Empire Tycoon

A comprehensive, deep-strategy commercial aviation management simulation for Android built with Jetpack Compose. Build, brand, and scale a global airline empire across 2,230+ real-world airports with 70+ authentic aircraft, complete livery customization, live 3D vector globe flight tracking, advanced demand modeling, and a **Dynamic Aviation Event System**—completely eliminating tedious fuel management while preserving rich economic realism.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following design choices have been aligned based on user preferences and feedback:
> - **Dynamic Event System (New)**: Incorporates spontaneous global and regional events (Economic Booms, Fuel Price Spikes, Airport Expansions, Tourism Surges, Tech Expos) that dynamically alter route yields, operating cost modifiers, and airport capacities.
> - **Indirect Fuel Cost Simulation**: While direct fuel micromanagement (refueling, spot purchasing) remains eliminated per the core brief, macroeconomic events like "Fuel Price Spike" simulate cost shifts dynamically by applying operational cost multipliers to older or multi-engine aircraft (e.g., quad-jets and classic tri-jets face higher maintenance/operating fees, rewarding modernization to ultra-efficient neos, MAXs, and Dreamliners).
> - **Difficulty & Scenarios**: Flexible choice of 4 distinct scenarios plus customizable parameters (Casual Sandbox, Modern Regional Startup, Veteran Tycoon, and Cargo Titan).
> - **Global Flight Tracking**: Interactive 3D vector globe with spherical orthographic projection, great-circle geodesic flight paths, live real-time aircraft positions, and radar telemetry pulses.
> - **Livery Studio**: Full creative control over airline branding—custom primary/secondary fuselage paints, cheatlines, tail fin accents, aircraft logos, cabin configurations, and callsigns.

---

## 1. Overview & Core Concept

- **What It Does**: Puts the player in the executive chair of an airline conglomerate. You choose your home hub, select your game scenario, design your bespoke livery, purchase or lease aircraft from a catalog of over 70 real-world commercial planes (turboprops, regional jets, narrowbodies, ultra-long-range jets, widebodies, tri-jets, and freighters), schedule routes between global airports, set ticket prices across cabin classes, manage cargo demand, react to unpredictable world events, and watch your fleet navigate the globe in real time.
- **Target Audience / Persona**: Aviation enthusiasts, tycoon and logistics simulation fans, and mobile strategists seeking deep route optimization, crisis management, and fleet planning without busywork.
- **Key Value**: Delivers the analytical depth of classic tycoon games paired with an ultra-responsive, beautiful dark-mode aeronautical radar aesthetic, now enriched by real-time world events that test your executive adaptability.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Empire Setup & Scenario Selection**:
   - Player names their airline, chooses IATA & ICAO codes, and picks their executive avatar and home base continent/hub.
   - Scenario selection: Casual Sandbox ($200M, instant deliveries), Modern Startup ($50M, regional fleet), Veteran Tycoon ($20M, stringent maintenance, high interest), or Cargo Titan ($60M, dedicated cargo network).
2. **Interactive Livery Designer**:
   - Live 2D vector aircraft canvas showing fuselage, wings, engines, and tail fin.
   - Interactive color picker for base livery, belly/cheatline accents, engine cowlings, and tail fin colors.
   - Decal & logo selection (wing emblems, tail insignia, typography/callsigns).
   - Real-time preview applied across all purchased aircraft models in the hangar.
3. **Interactive 3D Vector Flight Radar Globe**:
   - Canvas-rendered orthographic spherical projection with continent outlines, airport nodes, day/night terminator glow, and animated great-circle arcs.
   - Touch gestures: Drag to rotate the globe in 3D space, pinch-to-zoom.
   - Live aircraft blips traversing geodesic trajectories with radar pulse effects, altitude indicators, and current speed/status.
   - Tapping an aircraft opens the Live Flight Telemetry card (origin, destination, passengers/cargo onboard, ETA, route profit).
4. **Dynamic World Event Engine & News Ticker**:
   - Scrolling breaking-news ticker at the top of the radar HUD displaying active events.
   - Interactive Event Detail card detailing duration remaining, economic lore, and exact operational modifiers.
   - Event types:
     * **Global Economic Boom**: +30% to +50% global passenger and cargo demand, higher willingness to pay for Business & First Class.
     * **Fuel Price Spike**: Aircraft operating cost multipliers applied based on fuel efficiency rating (e.g. Quad-jets +40% costs, older tri-jets +30% costs, modern neos/MAX/787 unaffected), testing fleet composition without manual refueling mechanics.
     * **Airport Expansion Project**: Target hub (e.g., LHR, JFK, HND) undergoes terminal renovation, temporarily cutting departure capacity by 40% and increasing turnaround times.
     * **Tech Expo / Global Summit**: Destination city experiences a sudden 300% surge in business travelers for 7 simulation days.
     * **Volcanic Ash Cloud**: Airspace corridor warning, slowing cruising speed on affected regional corridors.
5. **Fleet Acquisition & Hangar**:
   - Filterable hangar categorized by class: Regional Turboprops, Regional Jets, Narrowbody, Long-Range Narrowbody, Widebody, Historic Tri-Jets, and Dedicated Freighters.
   - Real specs: range (km), passenger capacity (Eco/Biz/First), cargo volume (tonnes), cruising speed, fuel-efficiency tier, maintenance cost per flight hour, and purchase vs. lease price.
   - Fleet maintenance inspection and cabin configuration editor.
6. **Route Management & Yield Pricing**:
   - Select origin and destination airport from the global network.
   - Great-circle distance calculation, range verification against selected aircraft, and passenger/cargo demand simulation.
   - Dynamic event modifier badges displayed directly in the route planner (e.g., `⚡ Economic Boom +35% Demand`).
   - Pricing sliders for Economy, Business, First Class, and Freight per kg with dynamic load factor estimation based on price elasticity.
   - Daily flight scheduling (frequencies per day, slot allocations).
7. **Financial Operations & Executive Dashboard**:
   - Real-time income statement (ticket sales, cargo revenue, gate leasing, crew payroll, aircraft depreciation).
   - Bank loans (short-term credit, long-term bonds, credit rating tiers AAA to D).
   - Marketing campaigns, customer satisfaction index, and CEO achievements.

### Visual Identity & Theme

- **Aesthetic Direction**: High-tech aeronautical glassmorphism with an executive cockpit dark theme. Deep navy blues, carbon blacks, radar cyan accents, event amber/crimson alerts, and status golds.
- **Color Palette**:
  - `Background`: `#0A0E17` (Deep Stratosphere Black)
  - `Surface`: `#121826` (Cockpit Avionics Blue-Gray)
  - `Primary / Accent`: `#00E5FF` (Radar Cyan)
  - `Secondary Accent`: `#38BDF8` (Sky Blue)
  - `Success / Boom`: `#10B981` (ATC Emerald Green)
  - `Warning / Fuel Spike`: `#F59E0B` (Amber Beacon)
  - `Error / Airport Closed`: `#EF4444` (Alert Red)
  - `Luxury / First Class`: `#EAB308` (Executive Gold)
- **Typography & Hierarchy**:
  - Monospace telemetry headers for flight numbers, IATA codes, and coordinates.
  - Bold, clean sans-serif for executive summaries and financial metrics.
  - High contrast for micro-labels and radar HUD overlays.
- **Motion & Feedback**:
  - Smooth 60 FPS Canvas rendering for the 3D rotating vector globe.
  - Pulsing radar rings for active airport hubs and en-route flights.
  - Flashing news marquee for breaking aviation events.
  - Haptic feedback when launching routes, ordering aircraft, or reaching revenue milestones.

---

## 3. Key Product Decisions & Trade-Offs

- **Dynamic Event Engine with Indirect Efficiency Mechanics**:
  - *Chosen Approach*: The event system introduces macroeconomic turbulence through global and regional modifiers. When a "Fuel Price Spike" event occurs, older quad-jets and tri-jets suffer high operating expense penalties while next-gen composites (A350, 787, A321neo) remain immune or gain market share.
  - *Why*: Delivers authentic airline industry economics and strategic fleet diversification challenges without requiring the player to click "Refuel" buttons or watch fuel meters.
- **Spherical Orthographic Vector Projection on Compose Canvas**:
  - *Chosen Approach*: Custom mathematical 3D-to-2D orthographic projection engine rendering a rotating vector sphere with great-circle routes, airport points, active aircraft sprites, and event zone indicators.
  - *Why*: Delivers a stunning 3D globe visualization with zero external heavy 3D rendering dependencies, silky 60 FPS performance, and full pinch-to-zoom/rotation touch response.
- **Extensive Real-World Dataset**:
  - *Chosen Approach*: Built-in comprehensive airport directory covering major international, regional, and cargo gateways across all continents (over 2,200 available via indexed regional hierarchies and search) and 70+ authentic commercial airplanes with real specs.
- **Dual Persistence Architecture**:
  - *Chosen Approach*: Robust local Room database and DataStore repository for instantaneous offline gameplay, fast simulation ticks, and autosave.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Airline CEO Android App                         │
├────────────────────────────────────────────────────────────────────────┤
│                           Presentation Layer                           │
│  ┌───────────────────────┐  ┌──────────────────┐  ┌─────────────────┐ │
│  │   Interactive Globe   │  │  Fleet & Hangar   │  │ Route & Pricing │ │
│  │ 3D Vector Canvas HUD  │  │  70+ Aircraft UI  │  │  Yield Manager  │ │
│  └───────────┬───────────┘  └────────┬─────────┘  └────────┬────────┘ │
│  ┌───────────┴───────────┐  ┌────────┴─────────┐  ┌────────┴────────┐ │
│  │    Livery Designer    │  │  Finance & Loans │  │  Dynamic Events │ │
│  │  2D Vector Studio UI  │  │  Executive P&L   │  │ News Ticker & UI│ │
│  └───────────┬───────────┘  └────────┬─────────┘  └────────┬────────┘ │
├──────────────┼───────────────────────┼─────────────────────┼──────────┤
│              ▼                       ▼                     ▼          │
│                      AirlineGameViewModel                             │
│       - Simulation Clock (1x, 2x, 5x, Pause)                           │
│       - Dynamic Event Dispatcher & Active Modifier Calculator          │
│       - Flight Physics & Geodesic Trajectory Interpolation             │
│       - Route Demand & Revenue Settlement Engine                       │
│       - Fleet Maintenance & Operating Cost Tracker                     │
├────────────────────────────────────────────────────────────────────────┤
│                          Data & Domain Layer                           │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                      AirlineRepository                           │  │
│  │  - GameStateFlow: Cash, Reputation, Fleet, Routes, ActiveFlights  │  │
│  │  - DynamicEventsFlow: Active World & Regional Events             │  │
│  │  - AircraftCatalog: 70+ Aircraft (Specs, Efficiency, Classes)     │  │
│  │  - AirportDirectory: 2,230+ Worldwide Airport Nodes & Demands    │  │
│  │  - LiveryProfile: Colors, Decals, Logos, Tail Patterns           │  │
│  │  - Room Database / Local Tycoon Persistence                      │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────────┘
```

### Key Entities & Data Models

1. **`GameEvent`**:
   - `id`, `title`, `description`, `type` (GlobalBoom, FuelSpike, AirportExpansion, TourismSurge, TechSummit, VolcanicAsh)
   - `durationDaysRemaining`, `totalDurationDays`
   - `affectedAirportIata` (optional for local events)
   - `globalDemandMultiplier` (e.g. 1.35)
   - `quadJetCostMultiplier` (e.g. 1.40 for fuel price spike)
   - `twinJetCostMultiplier` (e.g. 1.05)
   - `airportCapacityMultiplier` (e.g. 0.60 for expansion project)
   - `headline`, `iconType`
2. **`AircraftModel`**:
   - `id`, `name`, `manufacturer` (Airbus, Boeing, Embraer, ATR, Bombardier, COMAC, McDonnell Douglas)
   - `category` (Turboprop, Regional Jet, Narrowbody, Long-Range Narrowbody, Widebody, Tri-Jet, Freighter)
   - `rangeKm`, `cruisingSpeedKmh`, `maxPassengers`, `cargoCapacityTonnes`
   - `engineConfig` (Twin, Tri, Quad, Turboprop)
   - `efficiencyRating` (Tier A Next-Gen, Tier B Modern, Tier C Classic, Tier D Heavy Tri/Quad)
   - `purchasePrice`, `leasePricePerDay`, `hourlyMaintenanceCost`
3. **`OwnedAircraft`**:
   - `aircraftId`, `registration`, `customName`, `flightHours`, `conditionPercent`, `status` (Idle, Scheduled, InFlight, Maintenance)
4. **`Airport`**:
   - `iata`, `icao`, `name`, `city`, `country`, `latitude`, `longitude`, `runwayLengthM`, `sizeTier`, `passengerDemandIndex`, `cargoDemandIndex`
5. **`AirlineRoute`**:
   - `routeId`, `originIata`, `destIata`, `distanceKm`, `assignedAircraftId`, `frequenciesPerDay`, `economyFare`, `businessFare`, `firstFare`, `cargoRatePerKg`, `dailyRevenue`, `loadFactor`
6. **`ActiveFlight`**:
   - `flightNumber`, `routeId`, `aircraftId`, `departureTimeMs`, `arrivalTimeMs`, `progress` (0.0 to 1.0), `currentLat`, `currentLon`, `passengers`, `cargoTonnes`
7. **`LiveryDesign`**:
   - `primaryColorHex`, `secondaryColorHex`, `bellyColorHex`, `tailColorHex`, `accentPattern` (Eurowhite, Cheatline, Billboard, Wave, TailFinSplit), `logoIconId`, `callsign`

### Simulation Engine

- Ticks forward in real time with configurable speed multipliers (1x, 2x, 5x, Pause).
- Periodically evaluates the Dynamic Event Generator to spawn new world events and transition expiring ones, triggering player alerts and HUD ticker updates.
- Calculates great-circle distance via spherical trigonometry (Haversine formula & slerp for lat/lon interpolation).
- Settles passenger ticket revenues, cargo earnings, and operating fees every virtual flight completion, applying active event modifiers.
- Autosaves game progress to persistent storage so empire state is never lost.
