# Optimizacija izvajanja mikrostoritev z uporabo GraalVM

Zahteve:
- Ustrezno vzpostavljeno GraalVM okolje
- Inštalirano orodje Maven

Za izvajanje primerov je potrebno v GraalVM predhodno naložiti Python, R in Ruby z ukazom:

`gu install python R ruby`

Pred prvim zagonom je potrebno namestiti zahtevane javanske knjičnice z izvedbo ukaza
`mvn install`

Prevod in izvedbo demonstracijske aplikacije dosežemo z:
`./build.sh && ./run.sh`

`build.sh` javanski program prevede in eksplodirane razredne datoteke pretvori v native image, `run.sh` pa native image zažene.

S posameznimi jeziki sta demonstrirani dve funkcionalnosti:
- izvedba lambde, ki vrne število 42
- dostopanje do atributov objekta, narejenega v gostujočem programskem jeziku.
Pri vsakem primeru je dodan tudi čas izvedbe v ms.


Po zagonu aplikacije so izvedbe lambd lokalno na voljo na:
- [Python](http://localhost:8080/v1/python/lambda)
- [R](http://localhost:8080/v1/r/lambda)
- [Ruby](http://localhost:8080/v1/ruby/lambda)
- [JavaScript](http://localhost:8080/v1/js/lambda)

Po zagonu aplikacije so izvedbe dostopanja do atributov objektov lokalno na voljo na:
- [Python](http://localhost:8080/v1/python/object)
- [R](http://localhost:8080/v1/r/object)
- [Ruby](http://localhost:8080/v1/ruby/object)
- [JavaScript](http://localhost:8080/v1/js/object)
