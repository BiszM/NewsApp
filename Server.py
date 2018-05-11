# for web extraction
from bs4 import BeautifulSoup
import requests
import lxml
import urllib3, re

#firebase
from firebase import firebase
#scheduler
from apscheduler.schedulers.blocking import BlockingScheduler



# links of the site to scrape
url_lists = [
"https://www.theverge.com/tech",
"http://autoweek.com/car-news",
"http://www.techradar.com/news/mobile-computing/laptops",
"https://rideapart.com",
"https://techcrunch.com/mobile/"] 

# lists to save to data scraped from url
firebase = firebase.FirebaseApplication('https://technews-9c71e.firebaseio.com/')

# use of agents to ignore bot restriction
agents = {
    'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36'
    }

# scraping url data with beautiful soup
def scrape(url):
    get_page_data = requests.get(url, headers=agents)
    return BeautifulSoup(get_page_data.text, 'lxml')


# method to call other methods by identifying url name
def get_link(url):
    # clearing lists
    # getting html of url
    soup = scrape(url)

    # getting links from techcrunch
    if "techcrunch" in url:
        print("in techcrunch")
        # locating which part will be scrape 
        from_tech = soup.findAll("h2", {"class": "post-block__title"})[:20]

        # Extracting links
        for data in from_tech:
            for data in data.findAll("a", href=True):
                link = data["href"]
                print("link: " +link)
                get_data_from_tech(link)

                # getting links from autoweek
    elif "autoweek" in url:
        print("in autoweek")
        # locating which part will be scrape
        from_auto = soup.findAll("article", {"class": "story"})[:15]

        # Extracting links 
        for data in from_auto:
            for data in data.findAll("a", href=True)[:1]:
                link = "http://autoweek.com" + data["href"]
                print("link: " +link)
                get_data_from_autoweek(link)

    # getting links from autoweek
    elif "techradar" in url:
        print("in techradar")
        # locating which part will be scrape
        from_radar = soup.findAll("div", {"class": "listingResult"})
        for data in from_radar:
            for data in data.findAll("a", href=True):
                link = data['href']
                print("link: " +link)
                get_data_from_techradar(link)

    # getting links from rideapart
    elif "rideapart" in url:
        print("in rideapart")
        # locating which part will be scrape
        from_radar = soup.findAll("article", {"class": "ra-article-mini"})
        for data in from_radar:
            for data in data.findAll("a", href=True)[:1]:
                link = "https://rideapart.com" + data['href']
                print("link: " +link)
                get_data_from_rideapart(link)

    elif "verge" in url:
        print("in theverge")
        soup = scrape(url)
        # From verge
        from_verge = soup.findAll("h2", {"class": "c-entry-box--compact__title"})[:]
        for data in from_verge:
            for data in data.findAll("a", href=True)[:1]:
                link = data['href']
                print("link: " +link)
                get_data_from_verge(link)

    else:
        print("Wrong Input")

    # method to extract data from techcrunch


def get_data_from_tech(url):
    description = ''
    author = ''
    soup = scrape(url)
    # locating the contents to scrape
    # content = soup.find("div", {"class", "article-entry"})
    title_content = soup.find('title')
    video_content = soup.find('div', {'class': 'vdb_player'})
    img_content = soup.find('img', {"class": "article__featured-image"})
    desc_content = soup.find('div', {'class': 'article-content'}).findAll('p')[:5]
    find_author = soup.find('div', {'class': 'article__byline'}).find('a')
    # checking if the content is None or not
    if video_content != None:
        print("video content found")
    elif title_content != None and img_content != None and desc_content != None:
        # saving the data in list
        for content in desc_content:
            description = description + '\n' + content.text
        author = find_author.text
        title = title_content.text
        image = img_content["src"]

        post_news(author, title, image, url, description,
            "false", "false", "mobile")
    else:
        print("something is null")


# method to extract data from autoweek
def get_data_from_autoweek(url):
    description = ''
    author = ''
    soup = scrape(url)
    # locating the contents to scrape
    title_content = soup.find("div", {"class": "story-header"}).find("h1")
    img_content = soup.find('article', {"class": "story"}).find("img", {"class": "thumb"})
    desc_content = soup.find('section', {"class": "main-body"}).findAll("p")[3:7]
    find_author = soup.find('div', {'class': 'author-feature'})
    # checking if the content is None or not
    if title_content != None and img_content != None and desc_content != None:
        # saving the data in list
        for content in desc_content:
            description = description + '\n' + content.text
        if find_author.find("a"):
            author = "By " + find_author.find("a").text
            author = author.replace('\r\n', '')
            author = " ".join(author.split())
        elif find_author.find("span", {"class": "author-textonly"}):
            author = find_author.find("span", {"class": "author-textonly"}).text
            author = author.replace('\r\n', '')
            author = " ".join(author.split())
        else:
            author = "unknown"  
        title = title_content.text
        image = img_content["src"]

        post_news(author, title, image, url, description,
            "false", "false", "car")
    else:
        print("something is null")


# method to extract data from techradar
def get_data_from_techradar(url):
    description = ''
    author = ""
    soup = scrape(url)
    content = soup.find("section", {"class": "content-wrapper"})
    if content is None:
        print("content is none")
    else:
        # locating the contents to scrape
        title_content = soup.find('title')
        img_content = content.findAll('img')[:1]
        desc_content = content.findAll("p")[:6]
        find_author = soup.find('span', {'itemprop': 'name'})
        
        # checking if the content is None or not
        if title_content != None and img_content != None and desc_content != None:
            # saving the data in list
            for content in desc_content:
                description = description +'\n'+ content.text
            title_text = title_content.text[:-12] 
            title = title_text
            for img in img_content:
                image = img["src"]
            author = "By "+find_author.text

            post_news(author, title, image, url, description,
                "false", "false", "laptop")


# method to extract data from rideapart
def get_data_from_rideapart(url):
    description = ''
    soup = scrape(url)
    # locating the contents to scrape
    title_content = soup.find('title')
    img_content = soup.find('h1', {"class": "image-box"}).find("img")
    desc_content = soup.find("div", {"class": "entry-content"}).findAll("p")[:5]
    find_author = soup.find('a', {'rel': 'author'})
    # checking if the content is None or not
    if title_content != None and img_content != None and desc_content != None:
        # saving the data in list
        for content in desc_content:
            description = description + '\n' + content.text
        title = title_content.text[:-12]
        image = "http://" + img_content["src"]
        author = "By " + find_author.text
       
        post_news(author, title, image, url, description,
        "false", "false", "bike")


# method to extract data from verge
def get_data_from_verge(url):
    description = ''
    soup = scrape(url)
        
    title_content = soup.find('title')
    img_content = soup.find('span', {"class": "e-image__image"})
    desc_content = soup.find("div", {"class": "c-entry-content"})
    find_author = soup.find('span', {'class': 'c-byline__item'})
    # checking if the content is None or not
    if title_content != None and find_author != None and img_content != None and desc_content != None:
        # saving the data in list
        for content in desc_content.findAll("p")[:5]:
            description = description + '\n' + content.text
        title = title_content.text[:-12]
        image = img_content["data-original"]
        if find_author.find("a") is None:
            author = "The Verge"
        else:
            author = "By " + find_author.find("a").text          
        post_news(author, title, image, url, description,
            "false", "false", "home")

    # data to save
    
def post_news(author, title, image, website, description, bookmarked, save, newsType):
    data = {
        "author": author, "title": title, "image": image,
        "website": website, "description": description,
        "type": newsType }
    post = firebase.post('/news', data)

def scrape_data_from_url():
    print("clearing previous news...")
    firebase.delete('/news', None)
    print("scraping....")
    for url in url_lists:
        get_link(url)
        print("completed")
    print("waiting for next iteration")

scrape_data_from_url()

scheduler = BlockingScheduler()
scheduler.add_job(scrape_data_from_url, 'interval', hours=1)
scheduler.start()