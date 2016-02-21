using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Runtime.CompilerServices;
using System.Text;

public class Json
{
    public static object Deserialize(string json)
    {
        if (json == null)
        {
            return null;
        }
        Parser parser = new Parser(json);
        return parser.Parse();
    }

    public static string Serialize(object obj)
    {
        Serializer serializer = new Serializer(obj);
        return serializer.Serialize();
    }

    public static string SerializeHumanReadable(object obj)
    {
        Serializer serializer = new Serializer(obj)
        {
            makeHumanReadable = true
        };
        return serializer.Serialize();
    }

    private class Parser
    {
        private static Dictionary<string, int> f__switch_map7;
        private StringReader json;

        public Parser(string jsonData)
        {
            this.json = new StringReader(jsonData);
        }

        private void EatWhitespace()
        {
        Label_0000:
            switch (this.json.Peek())
            {
                case 9:
                case 10:
                case 13:
                case 0x20:
                    this.json.Read();
                    goto Label_0000;
            }
        }

        private TOKEN NextToken()
        {
            this.EatWhitespace();
            if (this.json.Peek() != -1)
            {
                switch (this.PeekChar())
                {
                    case '"':
                        return TOKEN.STRING;

                    case ',':
                        this.json.Read();
                        return TOKEN.COMMA;

                    case '-':
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        return TOKEN.NUMBER;

                    case ':':
                        return TOKEN.COLON;

                    case '[':
                        return TOKEN.SQUARED_OPEN;

                    case ']':
                        this.json.Read();
                        return TOKEN.SQUARED_CLOSE;

                    case '{':
                        return TOKEN.CURLY_OPEN;

                    case '}':
                        this.json.Read();
                        return TOKEN.CURLY_CLOSE;
                }
                string key = this.NextWord();
                if (key != null)
                {
                    int num;
                    if (f__switch_map7 == null)
                    {
                        Dictionary<string, int> dictionary = new Dictionary<string, int>(3);
                        dictionary.Add("false", 0);
                        dictionary.Add("true", 1);
                        dictionary.Add("null", 2);
                        f__switch_map7 = dictionary;
                    }
                    if (f__switch_map7.TryGetValue(key, out num))
                    {
                        switch (num)
                        {
                            case 0:
                                return TOKEN.FALSE;

                            case 1:
                                return TOKEN.TRUE;

                            case 2:
                                return TOKEN.NULL;
                        }
                    }
                }
            }
            return TOKEN.NONE;
        }

        private string NextWord()
        {
            StringBuilder builder = new StringBuilder();
            while (" \t\n\r{}[],:\"".IndexOf(this.PeekChar()) == -1)
            {
                builder.Append(this.ReadChar());
                if (this.json.Peek() == -1)
                {
                    break;
                }
            }
            return builder.ToString();
        }

        public object Parse()
        {
            return this.ParseValue();
        }

        private List<object> ParseArray()
        {
            List<object> list = new List<object>();
            this.json.Read();
        Label_0012:
            switch (this.NextToken())
            {
                case TOKEN.NONE:
                    return null;

                case TOKEN.COMMA:
                    goto Label_0012;

                case TOKEN.SQUARED_CLOSE:
                    return list;

                case TOKEN.NULL:
                    list.Add(null);
                    goto Label_0012;

                case TOKEN.TRUE:
                    list.Add(true);
                    goto Label_0012;

                case TOKEN.FALSE:
                    list.Add(false);
                    goto Label_0012;
            }
            object item = this.ParseValue();
            list.Add(item);
            goto Label_0012;
        }

        private object ParseNumber()
        {
            string s = this.NextWord();
            if (s.IndexOf('.') == -1)
            {
                return long.Parse(s);
            }
            return double.Parse(s);
        }

        private Dictionary<string, object> ParseObject()
        {
            Dictionary<string, object> dictionary = new Dictionary<string, object>();
            this.json.Read();
            while (true)
            {
                TOKEN token2 = this.NextToken();
                switch (token2)
                {
                    case TOKEN.NONE:
                        return null;

                    case TOKEN.CURLY_CLOSE:
                        return dictionary;
                }
                if (token2 != TOKEN.COMMA)
                {
                    string str = this.ParseString();
                    if (str == null)
                    {
                        return null;
                    }
                    if (this.NextToken() != TOKEN.COLON)
                    {
                        return null;
                    }
                    this.json.Read();
                    dictionary[str] = this.ParseValue();
                }
            }
        }

        private string ParseString()
        {
            StringBuilder builder = new StringBuilder();
            this.json.Read();
            bool flag = false;
        Label_0014:
            if (this.json.Peek() != -1)
            {
                char ch = this.ReadChar();
                switch (ch)
                {
                    case '"':
                        flag = true;
                        goto Label_016C;

                    case '\\':
                        if (this.json.Peek() == -1)
                        {
                            goto Label_016C;
                        }
                        ch = this.ReadChar();
                        switch (ch)
                        {
                            case '"':
                                builder.Append('"');
                                goto Label_0014;

                            case '\\':
                                builder.Append('\\');
                                goto Label_0014;

                            case '/':
                                builder.Append('/');
                                goto Label_0014;

                            case 'b':
                                builder.Append('\b');
                                goto Label_0014;

                            case 'f':
                                builder.Append('\f');
                                goto Label_0014;

                            case 'n':
                                builder.Append('\n');
                                goto Label_0014;

                            case 'r':
                                builder.Append('\r');
                                goto Label_0014;

                            case 't':
                                builder.Append('\t');
                                goto Label_0014;
                        }
                        if (ch == 'u')
                        {
                            StringBuilder builder2 = new StringBuilder();
                            for (int i = 0; i < 4; i++)
                            {
                                builder2.Append(this.ReadChar());
                            }
                            builder.Append((char)Convert.ToInt32(builder2.ToString(), 0x10));
                        }
                        goto Label_0014;
                }
                builder.Append(ch);
                goto Label_0014;
            }
        Label_016C:
            if (!flag)
            {
                return null;
            }
            return builder.ToString();
        }

        private object ParseValue()
        {
            switch (this.NextToken())
            {
                case TOKEN.CURLY_OPEN:
                    return this.ParseObject();

                case TOKEN.SQUARED_OPEN:
                    return this.ParseArray();

                case TOKEN.STRING:
                    return this.ParseString();

                case TOKEN.NUMBER:
                    return this.ParseNumber();

                case TOKEN.TRUE:
                    return true;

                case TOKEN.FALSE:
                    return false;

                case TOKEN.NULL:
                    return null;
            }
            return null;
        }

        private char PeekChar()
        {
            return Convert.ToChar(this.json.Peek());
        }

        private char ReadChar()
        {
            return Convert.ToChar(this.json.Read());
        }

        private enum TOKEN
        {
            NONE,
            CURLY_OPEN,
            CURLY_CLOSE,
            SQUARED_OPEN,
            SQUARED_CLOSE,
            COLON,
            COMMA,
            STRING,
            NUMBER,
            TRUE,
            FALSE,
            NULL
        }
    }

    private class Serializer
    {
        private StringBuilder builder;
        private int depth;
        public bool makeHumanReadable;
        private object obj;

        public Serializer(object obj)
        {
            this.obj = obj;
            this.builder = new StringBuilder();
        }

        public string Serialize()
        {
            this.SerializeValue(this.obj);
            return this.builder.ToString();
        }

        private void SerializeArray(IList anArray)
        {
            this.builder.Append('[');
            bool flag = true;
            int num = 0;
            int count = anArray.Count;
            while (num < count)
            {
                object obj2 = anArray[num];
                if (!flag)
                {
                    this.builder.Append(',');
                }
                this.SerializeValue(obj2);
                flag = false;
                num++;
            }
            this.builder.Append(']');
        }

        private void SerializeObject(IDictionary obj)
        {
            bool flag = true;
            this.builder.Append(!this.makeHumanReadable ? "{" : "{\n");
            this.depth++;
            IEnumerator enumerator = obj.Keys.GetEnumerator();
            try
            {
                while (enumerator.MoveNext())
                {
                    object current = enumerator.Current;
                    if (!flag)
                    {
                        this.builder.Append(!this.makeHumanReadable ? "," : ",\n");
                    }
                    if (this.makeHumanReadable)
                    {
                        this.builder.Append(this.Tabs());
                    }
                    this.SerializeString(current.ToString());
                    this.builder.Append(!this.makeHumanReadable ? ":" : " : ");
                    this.SerializeValue(obj[current]);
                    flag = false;
                }
            }
            finally
            {
                IDisposable disposable = enumerator as IDisposable;
                if (disposable == null)
                {
                }
                disposable.Dispose();
            }
            this.depth--;
            if (this.makeHumanReadable)
            {
                this.builder.Append('\n');
                this.builder.Append(this.Tabs());
            }
            this.builder.Append('}');
        }

        private void SerializeOther(object value)
        {
            if ((((value is float) || (value is int)) || ((value is uint) || (value is long))) || ((((value is double) || (value is sbyte)) || ((value is byte) || (value is short))) || (((value is ushort) || (value is ulong)) || (value is decimal))))
            {
                this.builder.Append(value.ToString());
            }
            else
            {
                this.SerializeString(value.ToString());
            }
        }

        private void SerializeString(string str)
        {
            this.builder.Append('"');
            foreach (char ch in str.ToCharArray())
            {
                switch (ch)
                {
                    case '"':
                        this.builder.Append("\\\"");
                        break;

                    case '\\':
                        this.builder.Append(@"\\");
                        break;

                    case '\b':
                        this.builder.Append(@"\b");
                        break;

                    case '\f':
                        this.builder.Append(@"\f");
                        break;

                    case '\n':
                        this.builder.Append(@"\n");
                        break;

                    case '\r':
                        this.builder.Append(@"\r");
                        break;

                    case '\t':
                        this.builder.Append(@"\t");
                        break;

                    default:
                        {
                            int num2 = Convert.ToInt32(ch);
                            if ((num2 >= 0x20) && (num2 <= 0x7e))
                            {
                                this.builder.Append(ch);
                            }
                            else
                            {
                                this.builder.Append(@"\u" + Convert.ToString(num2, 0x10).PadLeft(4, '0'));
                            }
                            break;
                        }
                }
            }
            this.builder.Append('"');
        }

        private void SerializeValue(object value)
        {
            if (value == null)
            {
                this.builder.Append("null");
            }
            else if (value is IDictionary)
            {
                this.SerializeObject((IDictionary)value);
            }
            else if (value is IList)
            {
                this.SerializeArray((IList)value);
            }
            else if (value is string)
            {
                this.SerializeString((string)value);
            }
            else if (value is char)
            {
                this.SerializeString(((char)value).ToString());
            }
            else if (value is bool)
            {
                this.builder.Append(!((bool)value) ? "false" : "true");
            }
            else
            {
                this.SerializeOther(value);
            }
        }

        private string Tabs()
        {
            string str = string.Empty;
            for (int i = 0; i < this.depth; i++)
            {
                str = str + '\t';
            }
            return str;
        }
    }
}