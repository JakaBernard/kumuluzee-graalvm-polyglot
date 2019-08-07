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

Za jezik R sta lokalno na voljo tudi končni točki
1. [Lambda Divided](http://localhost:8080/v1/r/lambda-divided)
2. [Lambda Context](http://localhost:8080/v1/r/lambda-context)

Prva razdeli časovno meritev lambde na izgradnjo konteksta in izvedbo gostujoče kode.
Druga poizkusi uporabiti že prej ustvarjen in stalen kontekst, ki se ne ustvarja znova ob vsakem zahtevku.

Za jezik JavaScript so lokalno na voljo tudi točke
1. [Simple Data](http://localhost:8080/v1/js/simple-data)
2. [Custom String](http://localhost:8080/v1/js/custom-string)
3. [Object Keys](http://localhost:8080/v1/js/object-keys)
4. [Object Stringify](http://localhost:8080/v1/js/object-stringify)
5. [Array Functions](http://localhost:8080/v1/js/lambda-array-functions)
6. [Array From](http://localhost:8080/v1/js/lambda-array-functions-from)
7. [Is Array](http://localhost:8080/v1/js/is-array)
8. [Array Reduce](http://localhost:8080/v1/js/lambda-reduce)
9. [Own Array](http://localhost:8080/v1/js/own-array)

Prva vrne preprost objekt, pretvorjen v JSON niz. Ta se izvede uspešno.

Druga uporabi šablono za niz in vrne v niz združena podatka različnih tipov. Ta se izvede uspešno.

Tretja poizkusi dobiti imena atrtibutov podanega javanskega objekta, vendar neuspešno. Ta se izvede uspešno.

Četrta poizkusi podani javanski objekt pretvoriti v JSON niz znotraj JavaScript kode, vendar neuspešno.

Od pete do osme vrnejo napako, ali pa delovanje ni tako, kot bi moralo biti.
Peta poizkusi nad podano javansko tabelo izvajati funkcije, ki so v JavaScriptu pripete na vse tabele, vendar neuspešno.

Šesta poiuzkusi podano javansko tabelo pretvoriti v JavaScript tabelo z vgrajeno funkcionalnostjo, vendar neuspešno.

Sedma nad podano javansko tabelo izvede JavaScript funkcijo, ki preverja, ali je podani paramater tabela, vendar vrne napačen rezultat.

Osma ponovno poizkusi nad podano javansko tabelo izvesti funkcijo, ki je v JavaScriptu pripeta na vsako tabelo, vendar nesuperšno.

Deveta v gostovanem kontekstu ustvari JavaScript tabelo in nad njo izvede tabelsko operacijo, ki vse njene elemente zdriži v niz. Ta se izvede uspešno.

