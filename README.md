# CS253-Analysis-of-Programming-Languages
This repo includes my homework and notes based on the UCI-CS253 Analysis of Programming Language, taught by Professor Christina Lopes.

The general purpose of this course is to learn different kinds of programming styles. In order to have some hands on experience, the course has some homework which require us to write codes to extract the top 25 frequency words except for some stop words.
- The book of the course:[Booklink](https://www.amazon.com/Exercises-Programming-Style-Cristina-Videira/dp/1482227371/)
- URL link of the course:[Github](https://github.com/crista/exercises-in-programming-style)

The book has included 33 programming styles. During the quarter. I realized 16 of them with a language other than python, which is the language used to illustrate the coding style in the book. Besides the general style, some exercises also add some more complicated features in the exercise in the end of the chapter. The course also includes some after-class reading materials about how these programming styles are developed over time. Every week we have a reading material and I wrote a summary for it with some of my own thoughts. I got deeper understandings of different programming ways like functional programming, concurrent programming, etc.

## Week1
### Style 1: just realize of the basic function
- write a general program to realize the basic functions to extract the top 25 frequent words in pride-and-prejudice.
## Week2
### Style 4: CookBook
- No long jumps.
- Complexity of control flow tamed by dividing the large problem into smaller units using procedural abstraction. Procedures are pieces of func- tionality that may take input, but that don’t necessarily produce output that is relevant for the problem.
- Procedures may share state in the form of global variables.
- The larger problem is solved by applying the procedures, one after the other, that change, or add to, the shared state.
### Style 5: Pipeline
- Larger problem is decomposed using functionl abstraction. Functions take input, and produce output.
- No shared state between functions.
- The larger problem is solved by composing functions one after the other, in pipeline, as a faithful reproduction of mathematical function composition f ◦ g.
## Week3
### Style 7 Infinite Mirror
- All, or a significant part, of the problem is modeled using induction. That is, specify the base case (n0) and then the n + 1 rule.
### Style 8 Kick Forward
Variation of the Pipeline style, with the following additional constraints:
- Each function takes an additional parameter, usually the last, which is another function.
- That function parameter is applied at the end of the current function.
- That function parameter is given, as input, what would be the output
of the current function.
- The larger problem is solved as a pipeline of functions, but where the next function to be applied is given as parameter to the current function.
## Week4
### Style 11 Letter Box
- The larger problem is decomposed into things that make sense for the problem domain.
- Each thing is a capsule of data that exposes one single procedure, namely the ability to receive and dispatch messages that are sent to it.
◃ Message dispatch can result in sending the message to another capsule.
### Style 12 Closed Maps
- The larger problem is decomposed into things that make sense for the problem domain.
- Each thing is a map from keys to values. Some values are procedures/ functions.
- The procedures/functions close on the map itself by referring to its slots.
## Week5
### Style 14 Hollywood
- Larger problem is decomposed into entities using some form of abstrac- tion (objects, modules or similar).
- The entities are never called on directly for actions.
- The entities provide interfaces for other entities to be able to register
callbacks.
- At certain points of the computation, the entities call on the other en- tities that have registered for callbacks.
### Style 24 Quarantine
- Core program functions have no side effects of any kind, including IO.
- All IO actions must be contained in computation sequences that are
clearly separated from the pure functions.
- All sequences that have IO must be called from the main program.
## Week6
### Style 26 Spreadsheet
- The problem is modeled like a spreadsheet, with columns of data and formulas.
- Some data depends on other data according to formulas. When data changes, the dependent data also changes automatically.
### Style 27 Lazy Rivers
- Data is available in streams, rather than as a complete whole.
- Functions are filters/transformers from one kind of data stream to an-
other.
- Data is processed from upstream on a need basis from downstream.
## Week7
### Style 17 Reflective
- The program has access to information about itself, i.e. introspection.
- The program can modify itself – adding more abstractions, variables, etc. at runtime.
### Style 19 Plugins
- The problem is decomposed using some form of abstraction (procedures, functions, objects, etc.).
- All or some of those abstractions are physically encapsulated into their own, usually pre-compiled, packages. Main program and each of the packages are compiled independently. These packages are loaded dy- namically by the main program, usually in the beginning (but not nec- essarily).
- Main program uses functions/objects from the dynamically-loaded pack- ages, without knowing which exact implementations will be used. New implementations can be used without having to adapt or recompile the main program.
- Existence of an external specification of which packages to load. This can be done by a configuration file, path conventions, user input or other mechanisms for external specification of code to be loaded at runtime.
## Week8
### Style 28 Actors
- The larger problem is decomposed into things that make sense for the problem domain.
- Each thing has a queue meant for other things to place messages in it.
- Each thing is a capsule of data that exposes only its ability to receive
messages via the queue.
- Each thing has its own thread of execution independent of the others.
### Style 28.3 Combine Actors style and Lazy Rivers style
- Lazy Rivers, take 2. Languages like Java don’t have the yield state- ment explained in the Lazy Rivers style (Chapter 27). Implement the data-centric program in that chapter without using yield, and using the Actors style.
## Week9
### Style 29 Dataspaces
- Existence of one or more units that execute concurrently.
- Existence of one or more data spaces where concurrent units store and
retrieve data.
- No direct data exchanges between the concurrent units, other than via the data spaces.
### Style 31 Double Map Reduce
- Input data is divided in blocks.
- A map function applies a given worker function to each block of data,
potentially in parallel.
- The results of the many worker functions are reshuffled.
- The reshuffled blocks of data are given as input to a second map function that takes a reducible function as input.
- Optional step: a reduce function takes the results of the many worker functions and recombines them into a coherent output.
