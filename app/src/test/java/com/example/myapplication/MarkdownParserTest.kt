package com.example.myapplication

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.example.myapplication.ui.components.LanguageDetector
import com.example.myapplication.ui.components.MarkdownBlock
import com.example.myapplication.ui.components.MarkdownParser
import com.example.myapplication.ui.components.parseInlineMarkdown
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkdownParserTest {

    @Test
    fun testParseHeadings() {
        val markdown = """
            # Heading Level 1
            ## Heading Level 2
            ### Heading Level 3
            #### Heading Level 4
        """.trimIndent()

        val blocks = MarkdownParser.parse(markdown)
        assertEquals(4, blocks.size)

        val h1 = blocks[0] as MarkdownBlock.Heading
        assertEquals(1, h1.level)
        assertEquals("Heading Level 1", h1.text)

        val h2 = blocks[1] as MarkdownBlock.Heading
        assertEquals(2, h2.level)
        assertEquals("Heading Level 2", h2.text)

        val h3 = blocks[2] as MarkdownBlock.Heading
        assertEquals(3, h3.level)
        assertEquals("Heading Level 3", h3.text)

        val h4 = blocks[3] as MarkdownBlock.Heading
        assertEquals(4, h4.level)
        assertEquals("Heading Level 4", h4.text)
    }

    @Test
    fun testParseBulletAndNumberedLists() {
        val markdown = """
            - Bullet item 1
            * Bullet item 2
              - Nested bullet item
            1. First step
            2. Second step
        """.trimIndent()

        val blocks = MarkdownParser.parse(markdown)
        assertEquals(5, blocks.size)

        assertTrue(blocks[0] is MarkdownBlock.BulletItem)
        assertEquals("Bullet item 1", (blocks[0] as MarkdownBlock.BulletItem).text)
        assertEquals(0, (blocks[0] as MarkdownBlock.BulletItem).depth)

        assertTrue(blocks[1] is MarkdownBlock.BulletItem)
        assertEquals("Bullet item 2", (blocks[1] as MarkdownBlock.BulletItem).text)

        assertTrue(blocks[2] is MarkdownBlock.BulletItem)
        assertEquals("Nested bullet item", (blocks[2] as MarkdownBlock.BulletItem).text)
        assertEquals(1, (blocks[2] as MarkdownBlock.BulletItem).depth)

        assertTrue(blocks[3] is MarkdownBlock.NumberedItem)
        assertEquals("1", (blocks[3] as MarkdownBlock.NumberedItem).number)
        assertEquals("First step", (blocks[3] as MarkdownBlock.NumberedItem).text)

        assertTrue(blocks[4] is MarkdownBlock.NumberedItem)
        assertEquals("2", (blocks[4] as MarkdownBlock.NumberedItem).number)
        assertEquals("Second step", (blocks[4] as MarkdownBlock.NumberedItem).text)
    }

    @Test
    fun testParseCodeBlocks() {
        val markdown = """
            Here is a python snippet:
            ```python
            def binary_search(arr, target):
                low = 0
                high = len(arr) - 1
                return -1
            ```
            That was the code.
        """.trimIndent()

        val blocks = MarkdownParser.parse(markdown)
        assertEquals(3, blocks.size)

        assertTrue(blocks[0] is MarkdownBlock.Paragraph)

        val codeBlock = blocks[1] as MarkdownBlock.CodeBlock
        assertEquals("python", codeBlock.language)
        assertTrue(codeBlock.code.contains("def binary_search"))
        assertTrue(codeBlock.code.contains("return -1"))

        assertTrue(blocks[2] is MarkdownBlock.Paragraph)
    }

    @Test
    fun testParseTables() {
        val markdown = """
            | Step | Target | Middle | Action |
            |:---|:---:|---:|---|
            | 1 | 7 | 5 | Search right |
            | 2 | 7 | 7 | Found! |
        """.trimIndent()

        val blocks = MarkdownParser.parse(markdown)
        assertEquals(1, blocks.size)

        val table = blocks[0] as MarkdownBlock.Table
        assertEquals(listOf("Step", "Target", "Middle", "Action"), table.headers)
        assertEquals(2, table.rows.size)
        assertEquals(listOf("1", "7", "5", "Search right"), table.rows[0])
        assertEquals(listOf("2", "7", "7", "Found!"), table.rows[1])
    }

    @Test
    fun testParseBlockquoteAndDivider() {
        val markdown = """
            > Binary search requires a sorted array.
            > Always double check bounds!

            ---

            Regular text after divider.
        """.trimIndent()

        val blocks = MarkdownParser.parse(markdown)
        assertEquals(3, blocks.size)

        val quote = blocks[0] as MarkdownBlock.Blockquote
        assertTrue(quote.text.contains("Binary search requires a sorted array."))
        assertTrue(quote.text.contains("Always double check bounds!"))

        assertTrue(blocks[1] is MarkdownBlock.Divider)
        assertTrue(blocks[2] is MarkdownBlock.Paragraph)
    }

    @Test
    fun testInlineMarkdownTokenizer() {
        val text = "This is **bold** text, *italic* word, ***bold-italic***, and `val mid = 5` code."
        val annotated = parseInlineMarkdown(
            text = text,
            primaryColor = Color.White,
            secondaryColor = Color.Gray,
            codeColor = Color.Cyan,
            codeBgColor = Color.DarkGray,
            linkColor = Color.Blue,
            monoFont = FontFamily.Monospace
        )

        // Raw delimiters should not be present in final rendered text
        assertNotNull(annotated)
        val plain = annotated.text
        assertTrue("Rendered plain text should contain bold", plain.contains("bold text"))
        assertTrue("Rendered plain text should contain italic", plain.contains("italic word"))
        assertTrue("Rendered plain text should contain bold-italic", plain.contains("bold-italic"))
        assertTrue("Rendered plain text should contain val mid = 5", plain.contains("val mid = 5"))
        assertTrue("Raw backtick delimiters should be stripped", !plain.contains("`val mid = 5`"))
    }

    @Test
    fun testComplexAiResponseParsing() {
        val aiResponse = """
            ### ️ Binary Search Explained Simply

            Binary search is a fast algorithm to find an item in a **sorted list**.

            ---

            ### 🌟 Example: Finding `7` in `[1, 3, 5, 7, 9]`

            | Step | Middle | Target | Action |
            |------|--------|--------|--------|
            | 1    | `5`    | `7`    | Search right |
            | 2    | `7`    | `7`    | Found! |

            #### Step-by-Step Flow:
            1. Find middle index.
            2. Compare with target.
            3. Repeat on sub-array.

            - **Time Complexity**: ${'$'}O(\log n)${'$'}
            - **Space Complexity**: ${'$'}O(1)${'$'}

            ```python
            def binary_search(arr, target):
                return 42
            ```

            > Remember: The input list must always be sorted!
        """.trimIndent()

        val blocks = MarkdownParser.parse(aiResponse)
        assertTrue("Blocks should be extracted", blocks.isNotEmpty())

        val headingCount = blocks.count { it is MarkdownBlock.Heading }
        assertTrue("Headings parsed", headingCount >= 3)

        val tableCount = blocks.count { it is MarkdownBlock.Table }
        assertEquals("Table parsed", 1, tableCount)

        val codeBlockCount = blocks.count { it is MarkdownBlock.CodeBlock }
        assertEquals("Code block parsed", 1, codeBlockCount)

        val listCount = blocks.count { it is MarkdownBlock.NumberedItem || it is MarkdownBlock.BulletItem }
        assertTrue("List items parsed", listCount >= 5)

        val quoteCount = blocks.count { it is MarkdownBlock.Blockquote }
        assertEquals("Blockquote parsed", 1, quoteCount)
    }

    @Test
    fun testLanguageDetectorResolution() {
        assertEquals("PYTHON", LanguageDetector.resolve("python", ""))
        assertEquals("PYTHON", LanguageDetector.resolve("py", ""))
        assertEquals("KOTLIN", LanguageDetector.resolve("kotlin", ""))
        assertEquals("KOTLIN", LanguageDetector.resolve("kt", ""))
        assertEquals("JAVA", LanguageDetector.resolve("java", ""))
        assertEquals("C++", LanguageDetector.resolve("cpp", ""))
        assertEquals("C++", LanguageDetector.resolve("c++", ""))
        assertEquals("JAVASCRIPT", LanguageDetector.resolve("javascript", ""))
        assertEquals("JAVASCRIPT", LanguageDetector.resolve("js", ""))
        assertEquals("TYPESCRIPT", LanguageDetector.resolve("ts", ""))
        assertEquals("SQL", LanguageDetector.resolve("sql", ""))
        assertEquals("C#", LanguageDetector.resolve("csharp", ""))
    }

    @Test
    fun testLanguageDetectorAutoDetectionFromCodeContent() {
        val pythonCode = """
            def linear_search(arr, target):
                # Search element by element
                for idx, val in enumerate(arr):
                    if val == target:
                        return idx
                return -1
        """.trimIndent()
        assertEquals("PYTHON", LanguageDetector.resolve("", pythonCode))

        val kotlinCode = """
            fun binarySearch(arr: IntArray, target: Int): Int {
                var low = 0
                var high = arr.size - 1
                return -1
            }
        """.trimIndent()
        assertEquals("KOTLIN", LanguageDetector.resolve("", kotlinCode))

        val javaCode = """
            public class Solution {
                public static void main(String[] args) {
                    System.out.println("Hello DSA");
                }
            }
        """.trimIndent()
        assertEquals("JAVA", LanguageDetector.resolve("", javaCode))

        val cppCode = """
            #include <iostream>
            using namespace std;
            int main() {
                cout << "C++ implementation" << endl;
                return 0;
            }
        """.trimIndent()
        assertEquals("C++", LanguageDetector.resolve("", cppCode))
    }

    @Test
    fun testMultipleCodeBlocksWithDifferentLanguagesInOneResponse() {
        val multiCodeMarkdown = """
            ### Implementation in Python and Java

            Here is the **Python** solution:
            ```python
            def bubble_sort(arr):
                n = len(arr)
                for i in range(n):
                    # Flag for optimization
                    swapped = False
                    for j in range(0, n - i - 1):
                        if arr[j] > arr[j + 1]:
                            arr[j], arr[j + 1] = arr[j + 1], arr[j]
                            swapped = True
                    if not swapped:
                        break
                return arr
            ```

            And here is the **Java** solution:
            ```java
            public class BubbleSort {
                public static void sort(int[] arr) {
                    int n = arr.length;
                    for (int i = 0; i < n - 1; i++) {
                        for (int j = 0; j < n - i - 1; j++) {
                            if (arr[j] > arr[j + 1]) {
                                int temp = arr[j];
                                arr[j] = arr[j + 1];
                                arr[j + 1] = temp;
                            }
                        }
                    }
                }
            }
            ```

            Both run in O(n^2) worst case.
        """.trimIndent()

        val blocks = MarkdownParser.parse(multiCodeMarkdown)
        val codeBlocks = blocks.filterIsInstance<MarkdownBlock.CodeBlock>()

        assertEquals("Must parse exactly 2 code blocks", 2, codeBlocks.size)

        // Block 1: Python
        val pyBlock = codeBlocks[0]
        assertEquals("python", pyBlock.language)
        assertEquals("PYTHON", LanguageDetector.resolve(pyBlock.language, pyBlock.code))
        assertTrue(pyBlock.code.contains("def bubble_sort"))
        assertTrue(pyBlock.code.contains("# Flag for optimization"))
        assertTrue(pyBlock.code.contains("    for i in range(n):")) // 4 spaces indentation preserved
        assertTrue(pyBlock.code.contains("        if arr[j] > arr[j + 1]:")) // 8 spaces nested loop indentation preserved

        // Block 2: Java
        val javaBlock = codeBlocks[1]
        assertEquals("java", javaBlock.language)
        assertEquals("JAVA", LanguageDetector.resolve(javaBlock.language, javaBlock.code))
        assertTrue(javaBlock.code.contains("public class BubbleSort"))
        assertTrue(javaBlock.code.contains("int temp = arr[j];"))
    }
}
