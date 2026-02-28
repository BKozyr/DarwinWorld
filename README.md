# 🌍 Darwin World - Symulacja Ewolucyjna Ekosystemu

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-FF0000?style=for-the-badge&logo=java&logoColor=white)
![OOP](https://img.shields.io/badge/OOP-Principles-blue?style=for-the-badge)

## 📖 O Projekcie

https://github.com/user-attachments/assets/0abf9f23-6ea4-4dc8-a64b-ac21eeb1de19



Darwin World to oparta na programowaniu obiektowym symulacja ekosystemu, inspirowana procesami ewolucyjnymi opisanymi w książkach "Land of Lisp" oraz artykułach z "Scientific American". Projekt został zrealizowany w parach w ramach przedmiotu Programowanie Obiektywne na Akademii Górniczo-Hutniczej (AGH).

Symulacja generuje świat, w którym zwierzaki (roślinożercy) poruszają się, jedzą rośliny, rozmnażają się i przekazują swoje cechy genetyczne potomstwu (wraz z losowymi mutacjami). Środowisko jest dynamiczne, a przetrwanie zależy od poziomu energii poszczególnych osobników.

### ⚙️ Główne Mechaniki Symulacji
Każdy dzień w symulacji składa się z następujących faz:
1. **Śmierć:** Zwierzaki, których energia spadnie do zera, są usuwane z mapy.
2. **Ruch:** Zwierzaki obracają się zgodnie ze swoim aktywnym genem i przemieszczają się na sąsiednie pole.
3. **Jedzenie:** Zwierzaki zjadają rośliny na swoim polu, aby odzyskać energię. Konflikty o jedzenie rozstrzygane są na podstawie poziomu energii, wieku i liczby dzieci.
4. **Rozmnażanie:** Najedzone zwierzaki znajdujące się na tym samym polu mogą się rozmnażać. Genotyp potomka to kombinacja genów rodziców (proporcjonalna do ich energii) poddana losowym mutacjom.
5. **Wzrost roślin:** Na mapie wyrastają nowe rośliny.

### 🌱 Funkcja Specjalna: Wariant C (Uprawianie ziemi)
Ta konkretna implementacja zawiera **Wariant C: Uprawianie ziemi**. 
W przeciwieństwie do standardowej symulacji, gdzie dżungla znajduje się na równiku, ten wariant wprowadza dynamiczny system żyzności gleby:
* Miejsca, przez które przechodzą zwierzaki o dużej energii, stają się coraz bardziej żyzne.
* Gdy gleba osiągnie odpowiedni próg żyzności, tymczasowo staje się preferowaną strefą wzrostu roślin (dżunglą).
* Rośliny rosnące na żyznej glebie są większe i wystarczają na wiele posiłków (wiele wizyt zwierzaków), zanim znikną.

## 💻 Technologie
* **Język:** Java
* **GUI:** JavaFX
* **Architektura:** Programowanie Obiektywne (Wzorce projektowe, Clean Code, zasady SOLID)

## 📊 Funkcje i Interfejs Użytkownika (UI)
Aplikacja posiada rozbudowany interfejs graficzny, który pozwala na:
* Konfigurację parametrów symulacji (rozmiar mapy, początkowa liczba zwierzaków/roślin, wartości energii, wskaźniki mutacji).
* Uruchamianie wielu symulacji jednocześnie w osobnych oknach.
* Wstrzymywanie (Pause) i wznawianie (Resume) symulacji w dowolnym momencie.
* Śledzenie statystyk na żywo (całkowita liczba zwierzaków, roślin, średnia energia, najpopularniejsze genotypy).
* Śledzenie historii konkretnego zwierzaka (genom, wiek, liczba dzieci, zjedzone rośliny).

## 🚀 Jak uruchomić
1. Sklonuj repozytorium na swój komputer.
2. Zbuduj projekt w preferowanym środowisku IDE (zalecane IntelliJ IDEA) lub za pomocą narzędzia budującego (Gradle/Maven).
3. Upewnij się, że SDK JavaFX jest poprawnie skonfigurowane w Twoim środowisku.
4. Uruchom główną klasę aplikacji, aby włączyć menu konfiguracyjne.

---
*Projekt zrealizowany na przedmiot Programowanie Obiektywne (PO) na uczelni AGH.*
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
