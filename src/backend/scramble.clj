(ns backend.scramble)

(defn char-freqs
  "Returns the frequency of each character within the given string"
  [str]
  ;;; We could use group-by here and it would be more readable,
  ;;; but reduce will spare us growing a buffer for each char in the
  ;;; alphabet.
  (reduce (fn [acc ch]
            (update acc ch #(inc (or % 0))))
          {}
          str))

(defn scramble?
    "True if a portion of str1 characters can be rearranged to match
  str2, otherwise returns false"
  [str1 str2]
  ;;; To merely test whether there's a scrambling of str1 that contains str2
  ;;; as a substring, it's enough to show that there's as many of each character
  ;;; in str2 in str1, i.e. as many a's, as many c's, etc.
  (let [str1-freqs (char-freqs str1)
        str2-freqs (char-freqs str2)]
    (every? true? (for [[k str2-dim] str2-freqs
                        :let [str1-dim (get str1-freqs k)]]
                    (and str1-dim (>= (get str1-freqs k) str2-dim))))))
