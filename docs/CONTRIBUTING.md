#  Contributing (Quick Guide)

We welcome contributions to **ImagePickerKMP**!  

##  Steps to Contribute
1. **Fork & Clone**  
   ```bash
   git clone https://github.com/your-username/ImagePickerKMP.git
   cd ImagePickerKMP
   git checkout -b feature/your-feature
   ```
2. **Build & Test**  
   ```bash
   ./gradlew build test androidTest iosTest
   ./gradlew detekt   # run static analysis
   ```
3. **Code Style**  
   - Follow [Kotlin conventions](https://kotlinlang.org/docs/coding-conventions.html).  
   - 4 spaces indent, ≤120 chars per line, trailing commas.  
   - Use `expect/actual` for platform-specific code.  

4. **Commit & PR**  
   ```bash
   git commit -m "feat: add custom permission dialog"
   git push origin feature/your-feature
   ```
   - Open a PR with description, tests, and related issues.  

## Issues & Features
- **Bugs**: provide steps, expected vs actual, device/OS info.  
- **Features**: explain use case, proposal, alternatives.  

##  Branching
- `main` → production  
- `develop` → development  
- `feature/*`, `fix/*`, `release/*`, `hotfix/*`  

##  Help
- [Issues](https://github.com/ismoy/ImagePickerKMP/issues) • [Discussions](https://github.com/ismoy/ImagePickerKMP/discussions) • Discord  

---

 **TL;DR**: Fork → New Branch → Code + Tests → Run `./gradlew detekt` → Commit → PR 

---
