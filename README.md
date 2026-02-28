# PO_2025_PN1645_CIESIELCZYK_KOZYRA    
**Imię i nazwisko:** Szymon Ciesielczyk Bartosz Kozyra    
**Grupa i godzina zajęć**: gr.6 Poniedziałek 16:45      
**Diagram klas:**

```mermaid
classDiagram
    %% --- INTERFEJSY I ABSTRAKCJE ---
    class WorldElement {
        <<interface>>
        +getPosition() Vector2d
        +getResourcePath() String
    }
    
    class WorldMap {
        <<interface>>
        +place(Animal animal)
        +move(Animal animal)
        +objectAt(Vector2d position)
        +getElements() List~WorldElement~
        +getCurrentBounds() Boundary
        +getId() UUID
    }

    class MoveValidator {
        <<interface>>
        +canMoveTo(Vector2d position)
        +suggestPosition(Vector2d position) Vector2d
    }

    class AbstractWorldMap {
        <<abstract>>
        #Map~Vector2d, List~Animal~~ animals
        #MapVisualizer vis
        +registerObserver(MapChangeListener observer)
        +mapChanged(String msg)
    }

    %% --- KLASY DANYCH I LOGIKI ---
    class Vector2d {
        +x: int
        +y: int
        +add(Vector2d)
        +follows(Vector2d)
        +precedes(Vector2d)
    }

    class MapDirection {
        <<enumeration>>
        NORTH, SOUTH, EAST, WEST, ...
        +next()
        +toUnitVector()
    }

    class SimulationConfig {
        <<record>>
        +mapWidth: int
        +mapHeight: int
        +startEnergy: int
        +dailyEnergyCost: int
        +soilFertilityThreshold: int
        +superPlantLife: int
    }

    class Genotype {
        -genes: List~Integer~
        -currentGeneIdx: int
        +getCurrentGene() int
        +activateNextGene()
        +applyMutations(int count)
    }

    %% --- MODEL ---
    class Animal {
        -energy: int
        -age: int
        -childrenCount: int
        -dead: boolean
        -genotype: Genotype
        -direction: MapDirection
        +move(MoveValidator validator)
        +eat(int energy)
        +reproduce(Animal partner) Animal
        +addEnergy(int e)
        +isDead() boolean
    }

    class Plant {
        -position: Vector2d
        -energyValue: int
        -remainingBites: int
        +decreaseBite()
        +isConsumed() boolean
    }

    class CultivationMap {
        -plants: Map~Vector2d, Plant~
        -soilFertility: Map~Vector2d, Integer~
        -jungleTimers: Map~Vector2d, Integer~
        -config: SimulationConfig
        +growPlants()
        +updateSoilValidity()
        +consumePlant(Vector2d pos)
    }

    class Statistics {
        -animalCount: int
        -avgEnergy: double
        -avgLifespan: double
        +update(List~Animal~ animals, int deadCount)
    }

    %% --- SYMULACJA I UI ---
    class Simulation {
        -map: WorldMap
        -animals: List~Animal~
        -config: SimulationConfig
        -stats: Statistics
        +run()
        -removeDead()
        -moveAll()
        -eat()
        -reproduce()
    }

    class SimulationPresenter {
        -map: WorldMap
        -stats: Statistics
        +drawMap()
        +updateStats()
        +mapChanged()
    }

    class LauncherPresenter {
        +startSimulation()
    }

    %% --- RELACJE ---
    WorldMap <|.. AbstractWorldMap
    WorldMap --|> MoveValidator
    AbstractWorldMap <|-- CultivationMap

    WorldElement <|.. Animal
    WorldElement <|.. Plant
    
    AbstractWorldMap o-- Animal
    CultivationMap *-- Plant : composition

    Animal *-- Genotype : owns
    Animal o-- Vector2d
    Animal o-- MapDirection

    Simulation o-- WorldMap
    Simulation o-- SimulationConfig
    Simulation *-- Statistics
    
    SimulationPresenter --> Simulation : observes stats
    SimulationPresenter --> WorldMap : reads data
    LauncherPresenter ..> Simulation : creates
