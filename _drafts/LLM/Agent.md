# 大语言模型应用

## 当前有哪些主要大语言模型

| 模型 | 公司 | 国家 | 应用产品 |
| :--- | :--- | :--- | :--- |
| GPT | OpenAI | 美国 | ChatGPT(网页/App)、ChatGPT Work、[Codex](https://openai.com/codex/) |
| Claude | Anthropic | 美国 | Claude(网页/App)、Claude Code |
| Gemini | Google(DeepMind) | 美国 | Gemini(网页/App)、Gemini Omni |
| Grok | SpaceX | 美国 | Grok(网页/App)、Cursor |
| DeepSeek | 深度求索 | 中国 | DeepSeek(网页/App)、DeepSeek Harness |
| Qwen | 阿里巴巴 | 中国 | 千问(网页/App)、Qoder |
| Kimi | 月之暗面 | 中国 | Kimi(网页/App)、Kimi Work、Kimi Code |
| GLM | 智谱 | 中国(清华) | 智谱清言(网页/App)、ZCode |
| MiniMax | 稀宇 | 中国 | MiniMax(网页/App)、MiniMax Code、MiniMax Design |
| Doubao<br>Seedance | 字节跳动 | 中国 | 豆包(网页/App)、扣子(Agent)、Trae、剪映、即梦 |
| Hy/混元 | 腾讯 | 中国 | 元宝(网页/App)、WorkBuddy、CodeBuddy |

## 当前有哪些主要应用场景

| 场景 | 主要工具 | 样例 |
| :--- | :--- | :--- |
| 问答 - *网页或App交互* | DeepSeek、千问 | [whats_mlflow](bigdata-tools-python/tools/whats_mlflow) |
| 生成 - *生成文字、图片、视频等* | seedance、即梦 |  |
| 办公 - *Work Agent* | ChatGPT Work、Workbuddy、扣子 |  |
| 编程 - *Coding Agent* <br><br> *注：也就是 Agentic Coding。* | Qoder、claude code, cursor openclaw, codex, github copilot, <br>sonnet,Invisible AI to Cheat on Conversations,<br>https://cloud.tencent.com/developer/article/2649106,<br>https://bbs.huaweicloud.com/blogs/484999 | [Jim](https://github.com/liujianuuei/Jim) |
| 智能数据分析/智能BI | 业务人员无需掌握 SQL 等技能，只需用自然语言提问<br>（如“上个月销售额为什么下降”），<br>系统即可自动查询数据、生成图表并给出归因分析。 |  |
| 行业垂直应用 | 依赖行业知识。 | NA |


## AI Agent

> ...agent designed for longer, multi-step work and finished deliverables. —— https://help.openai.com/en/articles/20001275-chatgpt-work-and-codex

智能体（Agent）是**系统化**应用大语言模型解决复杂场景**多步骤**任务的一个工具。

### Agent 最佳实践

#### Skills

> With Skills, you can teach Codex your team’s standards, workflows, and ways of working. Codex applies them consistently across tasks, so it can contribute more effectively with less supervision. —— https://openai.com/codex/

> Skills are more than reusable instruction sets — they deploy agents. A skill gives Perplexity Computer a methodology for a type of task... ——https://www.perplexity.ai/help-center/en/articles/13914413-how-to-use-computer-skills

> Skills are reusable AI tools for specific jobs. ——https://www.genspark.ai/skills

#### Workflows

> Guided flows that turn complex tasks into simple steps. ——https://www.perplexity.ai/computer/workflows

Multi-agent workflows

#### Memory

> Perplexity automatically remembers useful details across conversations. ——https://www.perplexity.ai/computer/memory

#### Always-on background work

最终的目标，大模型从“你说任务，我具体执行”，过渡到“你说目标，我帮你做完”。Agent能够自主拆解复杂任务、调用外部工具、执行多步骤操作，例如自动分析财报并生成投资建议。

### 当前主要编程 Agent

编程 Agent 也称作 Coding agent, Agentic coding, Code with agents, AI coding, AI agent.

| 编程 Agent | 产品形态 | 适合场景 | 使用说明 |
| :--- | :--- | :--- | :--- |
| Codex | Codex in ChatGPT (Codex mode) (as a desktop app)<br>Codex IDE extension<br>Codex CLI<br>Codex web |  | [...](https://help.openai.com/en/articles/11369540-using-codex-with-your-chatgpt-plan) |
| Claude Code |  |  | [...](https://zhuanlan.zhihu.com/p/2028268722809316061) |
| Cursor |  |  |  |
| GitHub Copilot |  |  |  |

#### 科学上网工具

| bing.com搜索:科学上网工具 |
| :--- |
| [西部世界](https://fast88sj.com/i/sg045) |
| [北极星](https://beijixing.space/) |
| [飞鸟加速](https://47.243.132.233:3828/) |



### 当前主要办公 Agent

| 办公 Agent | 产品形态 | 适合场景 | 使用说明 |
| :--- | :--- | :--- | :--- |
| ChatGPT Work | ChatGPT(as a desktop app) |  |  |
| Workbuddy | Desktop App |  |  |
| 扣子 |  |  |  |

