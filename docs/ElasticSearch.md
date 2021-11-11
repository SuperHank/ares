# 认识ElasticSearch

## 1. 基本概念

### 索引（Index）

````
    ElasticSearch把数据存放到一个或者多个索引(indices)中。如果用关系型数据库模型对比，索引(index)的地位与数据库实例(database)相当。索引存放和读取的基本单元是文档(Document)。我们也一再强调，ElasticSearch内部用Apache Lucene实现索引中数据的读写。读者应该清楚的是：在ElasticSearch中被视为单独的一个索引(index)，在Lucene中可能不止一个。这是因为在分布式体系中，ElasticSearch会用到分片(shards)和备份(replicas)机制将一个索引(index)存储多份。
````

### 文档（Document）

````
    在ElasticSearch的世界中，文档是主要的存在实体。所有的ElasticSearch应用需求到最后都可以统一建模成一个检索模型：检索相关文档。文档由一个或者多个域(Field)组成，每个域(Field)由一个域名(此域名非彼域名)和一个或者多个值组成(有多个值的值称为多值域(multi-valued))。在ElasticSearch中，每个文档都可能会有不同的域(Field)集合；也就是说文档(Document)是没有固定的模式和统一的结构。文档(Document)之间保持结构的相似性即可。从客户端的角度来看，文档就是一个JSON对象。
````

### 域（Field）

````
    它是Document的组成部分，由两部分组成，名称(name)和值(value)。
````

### Term

````
    它是搜索的基本单位，其表现形式为文本中的一个词
````

### Token

````
    它是单个Term在所属Field中文本的呈现形式，包含了Term内容、Term类型、Term在文本中的起始及偏移位置。
````

### 参数映射(Mapping)

````
    所有的文档在存储之前都必须经过分析(analyze)流程。用户可以配置输入文本分解成Token的方式；哪些Token应该被过滤掉；或者其它的的处理流程，比如去除HTML标签。此外，ElasticSearch提供的各种特性，比如排序的相关信息。保存上述的配置信息，这就是参数映射(Mapping)在ElasticSearch中扮演的角色。尽管ElasticSearch可以根据域的值自动识别域的类型(field type)，在生产应用中，都是需要自己配置这些信息以避免一些奇的问题发生。要保证应用的可控性。
````

### 文档类型（Type）

````
    每个文档在ElasticSearch中都必须设定它的类型。文档类型使得同一个索引中在存储结构不同文档时，只需要依据文档类型就可以找到对应的参数映射(Mapping)信息，方便文档的存取。
````

### 节点（Node）

````
    单独一个ElasticSearch服务器实例称为一个节点。对于许多应用场景来说，部署一个单节点的ElasticSearch服务器就足够了。但是考虑到容错性和数据过载，配置多节点的ElasticSearch集群是明智的选择。
````

### 集群（Cluster）

````
    集群是多个ElasticSearch节点的集合。这些节点齐心协力应对单个节点无法处理的搜索需求和数据存储需求。集群同时也是应对由于部分机器(节点)运行中断或者升级导致无法提供服务这一问题的利器。ElasticSearch提供的集群各个节点几乎是无缝连接(即集群对外而言是一个整体，增加一个节点或者去掉一个节点对用户而言是透明的)。
````

### 分片索引（Shard）

````
    集群能够存储超出单机容量的信息。为了实现这样的需求，ES把数据分发到多个存储Lucene索引的物理机上。这些索引被称为分片索引，简称分片。在ES集群中，分片是自动完成的，并且所有分片是作为一个整体呈现给用户的。需要注意的是：尽管索引分片的过程是自动的，但是在应用中需要事先调整好参数。因为集群中分片的数量需要在索引创建前配置好，并且服务器启动后是无法修改的，至少目前无法修改。
````

### 索引副本（Replica）

````
    通过索引分片机制（Sharding）可以向ElasticSearch集群中导入超过单机容量的数据，客户端操作任意一个节点即可实现对集群数据的读写操作。当集群负载增长，用户搜索请求阻塞在单个节点上时，通过索引副本机制就可以解决这个问题。索引副本机制的思路很简单：为索引分片创建一份新的考本，它可以像原来的主分片一样处理用户搜索请求，同时也顺便保证了数据的安全性。即使主分片数据丢失，ElasticSearch也通过索引副本是的数据不丢失。与分片索引不同的是，索引副本可以随时添加或者删除，所以用户可以在需要的时候动态调整其数量。
````

### 时间之门（Gateway） ????

````
    在运行的过程中，ElasticSearch会收集集群的状态、索引的参数等信息。这些数据被存储在Gateway中。
````

## 2. ElasticSearch的工作原理

### 倒排索引

````
    ElasticSearch会把所有的信息都写入到一个称为"倒排索引"的数据结构中。这种数据结构把索引中的每个Term与相应的Document映射起来，这与关系型数据库存储数据的方式有很大的不同。读者可以把倒排索引想像成这样一个数据结构：数据以Term为导向，而不是以Document为导向。  
````

````
下面看看一个简单的倒排索引是什么样的。假定我们的Document只有title域（Field）被编入索引。Document如下：
document 1    ElasticSearch Servier
document 2    Mastering ElasticSearch
document 3    Apache Solr 4 Cookbook
````

| Term | Count | Docs |
|  :---- | :--:  | :---- |
| 4 | 1 | <document 3> | | Apache | 1 |<document 3> |
| Cookbook | 1|<document 3> |
| ElasticSearch | 2 |<document 1><document 2> |
| Mastering | 1 |<document 1> |
| Server | 1 |<document 1> |
| Solr | 1 |<document 3> |

````
    正如所看到的那样，每个词都指向它所出现的文档号。这样的存储方式使得高效的信息检索成为可能，比如基于词的检索（term-based query）。此外，每个词映射着一个数值count，它代表着Term在文档集中出现的频繁程度。
````

### 文本分析

````
    1. 传入到Document中的数据如何转变成倒排索引
    2. 查询语句如何转换成一个个Term使得高效率文本搜索变的可行
    这种转换数据的过程就称为文本分析。
    
    文本分析工作由Analyzer组件负责。Analyzer由一个分词器（Tokenizer）和>=0个过滤器（Filter）组成，也可能会有>=0个字符映射器（Character Mappings）组成。
````

#### 分词器（ Tokenizer）

````
    分词器用来把文本拆分成一个个Token。Token中包含了比较多的信息，比如Term在文本中的位置及Term原始文本，以及Term的长度。文本经过分词器处理后的结果称为Token Stream。Token Stream其实就是一个个Token的顺序排列。Token Stream 将等待着过滤器（Filter）来处理。
````

#### 过滤器（Filter）

````
    过滤器用来处理Token Stream中的每一个Token。处理方式包括删除Token，修改Token，甚至添加新的Token。ES中内置了许多过滤器 如：
    Lowercase Filter：把所有token中的字符都变成小写
    ASCII Folding Filter：去除token中非ASCII码的部分
    Synonyms Filter：根据同义词替换规则替换相应的Token
    ....
````

#### 索引过程

````
    ElasticSearch使用用户指定好的Analyzer解析用户添加的Document。当然Document中不同的Field可以指定不同的Analyzer。
````

#### 搜索过程

````
    用户的输入查询语句将被选定的查询解析器（Query Parser）所解析，生成多个Query对象。当然用户也可以选择不解析查询语句，是查询语句保留原始的状态。在ElasticSearch中，有的Query对象会被解析，有的不会，比如：前缀查询（Prefix Query）就不会被解析，精确匹配查询（Match Query）就会被解析。对使用者来说，梨节制一点至关重要。
    
    倒排索引中词应该和查询语句中的词正确匹配。如果无法匹配，那么ES也不会返回我们实际需要的结果。举个例子：如果在索引阶段对文本进行了转小写，那么查询语句也必须进行相同处理，不然就是竹篮打水。
````

## 3 查询语言

### 基础运算符

- **AND**： 给定两个Term，形成查询表达式。只有两个Term都匹配成功，查询子句才匹配成功。比如：查询语句 ````apache AND luncene```` 的意思是匹配包含apache切包含luncene的文档。
- **OR**：给定多个Term，只要其中一个匹配成功，其形成的查询表达式就匹配成功。比如：查询表达式：````apache OR luncene````能够匹配包含````apache````
  的文档，也能匹配包含````lucene````的文档，还能匹配同时包含这两个Term的文档。 如果Term之前没有指定运算符，那么默认使用OR。
- **NOT**：这意味着对于与查询语句匹配的文档，NOT运算符后面的Term就不能出现在文档中。比如：查询表达式````apache NOT lucene````就只能匹配包含````apache````
  且不包含````lucene````的文档
- ➕：这个符号表明：如果想要查询语句与文档匹配，那么给定的Term必须出现在文档中。例如：希望搜索到包含关键词lucene,最好能包含关键词apache的文档，可以用如下的查询表达式：````+lucene apache````。
- ➖
  ：这个符号表明：如果想要查询语句与文档匹配，那么给定的Term不能出现在文档中。例如：希望搜索到包含关键词lucene,但是不含关键词elasticsearch的文档，可以用如下的查询表达式：````+lucene -elasticsearch````
  。

### 复杂查询表达式

````
    查询表达式可以用小括号组合，形成复杂的查询表达式。比如：elasticsearch AND (mastering OR book)
````

### 多域查询

````
    文档中所有的数据都是存储在一个个的Field中的，多个Field形成一个文档。如果希望查询指定的Field，就需要在查询表达式中指定Field Name，后面接一个冒号，紧接着一个查询表达式。
    例如：查询title域中包含关键词elasticsearch的文档，查询表达式如下：title:elasticsearch
````

### 词语修饰符

````
    除了可以应用简单的关键词和查询表达式实现标准的域查询外，ES还支持往查询表达式中传入修饰符使关键词具有变形能力。最常用的修饰符，也就是通配符。ES支持?和\*两种通配符。
    ?可以匹配任意单个字符
    \*可以匹配任意多个字符
````

### 模糊查询

### 临近查询

### 特殊字符处理

````
    如果在搜索关键词中出现了以下字符集合中的任意一个字符，就需要用反斜杠\\进行转义。
    字符集如下:
    +,-,&&,||,!,(,),[,],{,},^,",~,*,?,:,\,/
    
    例如查询关键词 abc"efg 就需要转义成 abc\"efg 
````





















