/**
 * @file "Spreadsheet" programming style
 * @author Fuyao Li
 * @date 11/09/2018
 */
var fs = require('fs');


// input data
var all_words = [[], null];

var stop_words = [[], null];

// load the fixed data into the first 2 columns
var file = fs.readFileSync(process.argv[2], 'utf8');
var words = file.toString().replace(/[^a-zA-Z\\d\\s]/g, ' ');
all_words[0] = words.toLowerCase().split(' ');

var stop_words_file = fs.readFileSync('../stop_words.txt', 'utf8');
var stopWords = stop_words_file.toString();
stop_words[0]= stopWords.split(',');

// we use the second position function to calculate the first position element,
// then return it when excuting the update() function

var non_stop_words = [[], 
function() {
    var res = [];
        all_words[0].forEach((w) => {
            if (stop_words[0].indexOf(w) === -1 && w.length >= 2) {
                res.push(w);
            }
        });
        return res;
    }
];

//// unique non_stop words
var unique_words = [[],
function() {
    var res = [];
    non_stop_words[0].forEach((w) => {
        if (res.indexOf(w) === -1) {
            res.push(w);
        }
    });
    return res;
}];

var counts = [{},
function () {
        var word_freq = {};
        non_stop_words[0].forEach((word) => {
            // count the frequency of each word
            if (word_freq[word] !== undefined)
                word_freq[word] += 1;
            else
                word_freq[word] = 1;
        });
        return word_freq;
    }
];

var sorted_data = [[],
function () {
        var element = Object.keys(counts[0]).map(function (key) {
            return [key, counts[0][key]];
        });
        element.sort((key1, key2) => {return key2[1] - key1[1];});
        return element;
    }
];


// the entire spreadsheet
var all_columns = [all_words, stop_words, non_stop_words, unique_words, counts, sorted_data];

function update() {
    all_columns.forEach((c) => {
        if(c[1] != null)
        c[0] = c[1]();
    });
}


//// update the columns with formulas
update();
sorted_data[0].slice(0,25).forEach((element) => {console.log(element[0], ' - ', element[1]);});