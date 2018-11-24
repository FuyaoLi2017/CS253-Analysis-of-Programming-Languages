/**
 * @file "Lazy River" programming style, coroutine, generator
 * @author Fuyao Li
 * @date 11/10/2018
 */
let fs = require('fs');


function* characters(filename) {
    let chars = (fs.readFileSync(filename) + "").toLowerCase();
    for (let i = 0; i < chars.length; i++) {
        yield chars[i];
    }
}


function* all_words(filename) {
    let start_char = true;

    for (var char of characters(filename)) {

        if (start_char) {
            word = "";

            if (/^[a-z0-9]+$/i.test(char)) {
                word = char.toLowerCase();
                start_char = false;
            }
        }
        else {
            if (/^[a-z0-9]+$/i.test(char)) {
                word += char.toLowerCase();

            }
            else {
                start_char = true;

                yield word;
            }


        }

    }


}


function* non_stop_words(filename) {
    let stopWordFile = fs.readFileSync("../stop_words.txt") + "";
    let stop_words = stopWordFile.toLowerCase().split(",").concat("abcdefghijklmnopqrstuvwxyz".split(''));
    for (let word of all_words(filename)) {

        if (stop_words.indexOf(word) === -1) {
            yield word;
        }

    }

}


function* count_and_sort(filename) {


    let freqs = {};
    let i = 1;
    for (let word of non_stop_words(filename)) {
        if (freqs[word] === undefined)
            freqs[word] = 1;
        else
            freqs[word] += 1;
        
        // For every 5000 words that it processes, it yields its current word-frequency dictionary
        if (i % 5000 === 0) {
            // create an items object to be sorted.
            var element = Object.keys(freqs).map(function (key) {
            return [key, freqs[key]];
            });
            // sort items by value (frequency).
            element.sort((key1, key2) => {return key2[1] - key1[1];});
            // return the sorted items object.
            yield element;
        }
        i++;



    }

}

for (let word_freqs of count_and_sort(process.argv[2])) {

    word_freqs.slice(0,25).forEach((element) => {console.log(element[0], ' - ', element[1]);});
}
